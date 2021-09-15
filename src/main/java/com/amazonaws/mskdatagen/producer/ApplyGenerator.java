// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import com.amazonaws.mskdatagen.core.generator.ResultGen;
import com.amazonaws.mskdatagen.dto.context.GeneratedConfig;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.HistoryConfigs;
import com.amazonaws.mskdatagen.dto.context.TopicSeqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.mskdatagen.core.generator.DepsParameters;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;
import com.amazonaws.mskdatagen.dto.context.TopicConfigs;

public class ApplyGenerator {
    private static final Logger log = LoggerFactory.getLogger(ApplyGenerator.class);

    public static final long DEFAULT_MAX_HISTORY = 1000000L;
    public static final int MAX_FAILED_ATTEMPTS = 100;
    private final Random rand = new Random();

    private final Context context;

    public ApplyGenerator(Context context) {
        this.context = context;
    }

    public void advanceUntilSuccess() {
        advanceStep();
        int failedAttempts = 0;
        int throttledAttempts = 0;
        getContextRecur(failedAttempts, throttledAttempts);
    }

    private void getContextRecur(int failedAttempts, int throttledAttempts) {
        if (failedAttempts > MAX_FAILED_ATTEMPTS) {
            context.getContextMap().remove(ConfigType.TOPIC_SEQ_CONFIG);
            throw new IllegalStateException("Couldn't generate another event. State machine may be livelocked.");
        }

        if ("failed".equals(context.getGenerated().getState())) {
            advanceStep();
            failedAttempts++;
            getContextRecur(failedAttempts, throttledAttempts);
        } else if ("throttled".equals(context.getGenerated().getState())) {
            maybeBackoff(throttledAttempts);
            advanceStep();
            throttledAttempts++;
            getContextRecur(failedAttempts, throttledAttempts);
        }
    }

    private void maybeBackoff(int throttledAttempts) {
        long count = context.getGenerators().count();
        if (throttledAttempts > 0 && throttledAttempts % count == 0) {
            List<Long> nextTimestamps = new ArrayList<>();
            Map<String, Long> timestamps = context.getTimestampsConfig().getTimestamps();
            for (Map.Entry<String, Long> timestamp : timestamps.entrySet()) {
                nextTimestamps.add(context.getGenerators()
                        .filter(t -> t.getTopic().equals(timestamp.getKey()))
                        .findFirst().map(GeneratorsConfigs::getThrottleNs)
                        .map(ts -> ts + timestamp.getValue()).orElse(timestamp.getValue()));
            }

            long earliestTs = Collections.min(nextTimestamps);
            long now = System.nanoTime();

            long deltaMs = (long) (((earliestTs - now) < 0 ? 0 : (earliestTs - now)) * 0.000001);
            try {
                Thread.sleep(deltaMs);
            } catch (InterruptedException e) {
                log.error("Throttled Interrupted Exception.", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
    }

    private void advanceStep() {
        TopicSeqConfig topicSeqConfig = context.getTopicSeq().findFirst().orElse(null);
        if (topicSeqConfig != null) {
            Optional<GeneratorsConfigs> generatorsConfigs = context.getGenerators().filter(t -> t.getTopic().equals(topicSeqConfig.getTopic()))
                    .findFirst();
            Long lastTs = context.getTimestampsConfig().getTimestamps().get(topicSeqConfig.getTopic());
            Long throttle = generatorsConfigs.map(GeneratorsConfigs::getThrottleNs).orElse(0L);
            long nextTs = lastTs + throttle;
            long now = System.nanoTime();

            if (now >= nextTs) {
                List<String> dependencies = generatorsConfigs.map(GeneratorsConfigs::getDependencies).orElse(Collections.emptyList());
                Function<DepsParameters, ResultGen> keyGenF = generatorsConfigs.map(GeneratorsConfigs::getKeyF)
                        .orElse(input -> new ResultGen(true));
                Function<DepsParameters, ResultGen> valGenF = generatorsConfigs.map(GeneratorsConfigs::getValueF)
                        .orElse(input -> new ResultGen(true));

                Map<ResultGen, ResultGen> resultGenResultGenMap = invokeGenerator(context, dependencies, keyGenF, valGenF);
                Optional<Map.Entry<ResultGen, ResultGen>> successResult = resultGenResultGenMap.entrySet().stream()
                        .filter(result -> result.getKey().isSuccess() && result.getValue().isSuccess()).findFirst();

                if (successResult.isPresent()) {
                    successResult(context, successResult.get(), topicSeqConfig.getTopic());
                } else {
                    resultGenResultGenMap.forEach((key, value) -> {
                        String message = String.format("Failed topic: %s { key:%s, value: %s }", topicSeqConfig.getTopic(), key.isSuccess(), value.isSuccess());
                        log.error(message);
                    });

                    updateGeneratedState(context, "failed");
                    context.getContextMap().get(ConfigType.TOPIC_SEQ_CONFIG).remove(0);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Throttled topic: %s", topicSeqConfig.getTopic()));
                }

                updateGeneratedState(context, "throttled");
                context.getContextMap().get(ConfigType.TOPIC_SEQ_CONFIG).remove(0);
            }
        } else {
            log.error("Can't find topic.");
            updateGeneratedState(context, "drained");
        }
    }

    private void updateGeneratedState(Context context, String state) {
        context.getContextMap().remove(ConfigType.GENERATED_CONFIG);
        ContextConfig generatedConfig = new GeneratedConfig(false, state);
        context.getContextMap().put(ConfigType.GENERATED_CONFIG, Collections.singletonList(generatedConfig));
    }

    private void successResult(Context context, Map.Entry<ResultGen, ResultGen> result, String topic) {
        DepsParameters.NsKeyValue nsKeyValue
                = new DepsParameters.NsKeyValue(result.getKey().getResult(), result.getValue().getResult());

        Optional<Consumer<Context>> retireFn = context.getGenerators().filter(t -> t.getTopic().equals(topic))
                .map(GeneratorsConfigs::getRetireFn).findFirst();

        GeneratedConfig generatedConfig = new GeneratedConfig(true, topic, nsKeyValue.getKey(), nsKeyValue.getValue());
        context.getContextMap().put(ConfigType.GENERATED_CONFIG, Collections.singletonList(generatedConfig));

        context.getTimestampsConfig().getTimestamps().put(topic, System.nanoTime());

        updateHistory(context, topic, nsKeyValue);
        updateRecordsProduced(context, topic);

        retireFn.ifPresent(fn -> fn.accept(context));

        purgeHistory(context, topic);
    }

    private void updateHistory(Context context, String topic, DepsParameters.NsKeyValue nsKeyValue) {
        List<ContextConfig> contextConfigs = context.getContextMap().computeIfAbsent(ConfigType.HISTORY_CONFIG, k -> new ArrayList<>());

        HistoryConfigs historyConfigs = (HistoryConfigs) contextConfigs.stream().filter(t -> topic.equals(t.getTopic())).findFirst()
                .orElseGet(() -> {
                    HistoryConfigs hc = new HistoryConfigs(topic);
                    contextConfigs.add(hc);
                    return hc;
                });

        historyConfigs.getHistory().add(nsKeyValue);
    }

    private void updateRecordsProduced(Context context, String topic) {
        Map<String, Long> counters = context.getRecordsProducedConfig().getCounters();
        long count = counters.containsKey(topic) ? counters.get(topic) : 0;
        counters.put(topic, count + 1);
    }

    private Map<ResultGen, ResultGen> invokeGenerator(Context context, List<String> dependencies, Function<DepsParameters, ResultGen> keyGenF,
                                                      Function<DepsParameters, ResultGen> valGenF) {
        Map<String, DepsParameters.NsKeyValue> depTargets = genRowDependencies(context, dependencies);
        DepsParameters depsParameters = new DepsParameters(dependencies, depTargets);
        ResultGen keyResults = keyGenF.apply(depsParameters);
        ResultGen valueResults = valGenF.apply(depsParameters);
        return Collections.singletonMap(keyResults, valueResults);
    }

    private Map<String, DepsParameters.NsKeyValue> genRowDependencies(Context context, List<String> dependencies) {
        Map<String, DepsParameters.NsKeyValue> genRowDependenciesResult = new HashMap<>();
        for (String dependency : dependencies) {
            context.getHistories().filter(t -> dependency.equals(t.getTopic())).findFirst()
                    .ifPresent(t -> {
                        if (t.getHistory().isEmpty()) {
                            genRowDependenciesResult.put(dependency, null);
                        } else {
                            int randomIndex = rand.nextInt(t.getHistory().size());
                            DepsParameters.NsKeyValue nsKeyValue = t.getHistory().get(randomIndex);
                            genRowDependenciesResult.put(dependency, nsKeyValue);
                        }
                    });
        }

        return genRowDependenciesResult;
    }

    private void purgeHistory(Context context, String topic) {
        context.getHistories()
                .filter(t -> t.getTopic().equals(topic)).findFirst()
                .ifPresent(history -> {
                    long maxHistory = maxHistoryForTopic(context, topic);
                    if (history.getHistory().size() > maxHistory) {
                        history.resizeHistoryMap(maxHistory);
                    }
                });
    }

    private long maxHistoryForTopic(Context context, String topic) {
        return context.getTopicConfigs().filter(t -> topic.equals(t.getTopic()))
                .findFirst().map(TopicConfigs::getHistoryRecordsMax)
                .orElseGet(() -> Optional.ofNullable(context.getGlobalConfig().getHistoryRecordsMax())
                        .orElse(DEFAULT_MAX_HISTORY));
    }
}
