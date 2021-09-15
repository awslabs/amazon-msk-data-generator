// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.RecordsProducedConfig;
import com.amazonaws.mskdatagen.dto.context.RetiredTopicsConfig;
import com.amazonaws.mskdatagen.dto.context.TopicConfigs;

public class CompileRetireStrategy implements ContextInitializer {
    private final Context context;

    public CompileRetireStrategy(Context context) {
        this.context = context;
    }

    public void prepare() {
        List<GeneratorsConfigs> generators = context.getGenerators().collect(Collectors.toList());

        for (GeneratorsConfigs generator : generators) {
            Long n = context.getTopicConfigs()
                    .filter(t -> generator.getTopic().equals(t.getTopic())).map(TopicConfigs::getRecordsExactly)
                    .findFirst().orElse(0L);

            if (n == 0) {
                generator.setRetireFn(defaultRetireFn());
            } else {
                generator.setRetireFn(boundedRetireFn(generator.getTopic(), n));
            }
        }
    }

    private Consumer<Context> boundedRetireFn(String topic, Long n) {
        return ctx -> {
            long current = ctx.getContextMap().get(ConfigType.RECORDS_PRODUCED_CONFIG)
                    .stream().map(RecordsProducedConfig.class::cast)
                    .findFirst().map(RecordsProducedConfig::getCounters)
                    .map(t -> t.get(topic)).orElse(0L);

            if (current >= n) {
                ctx.getContextMap().computeIfAbsent(ConfigType.RETIRED_TOPICS_CONFIG, k -> new ArrayList<>())
                        .add(new RetiredTopicsConfig(topic));

                updateTopicSeq(ctx);
            } else {
                defaultRetireFn().accept(ctx);
            }
        };
    }

    private void updateTopicSeq(Context ctx) {
        //		long nTopics = context.getGenerators().count();
        // 		List<ContextConfig> contextConfigs = ctx.getContextMap().get(ConfigType.RETIRED_TOPICS_CONFIG);
        //		long retiredTopics = Optional.ofNullable(contextConfigs).map(List::size).orElse(0);
        //		long left = nTopics - retiredTopics - 1;

        List<ContextConfig> listToRemove = new ArrayList<>();
        ctx.getTopicSeq().findFirst().ifPresent(listToRemove::add);
        ctx.getTopicSeq().skip(2).forEach(listToRemove::add);
        ctx.getContextMap().get(ConfigType.TOPIC_SEQ_CONFIG)
                .removeAll(listToRemove);
    }

    private Consumer<Context> defaultRetireFn() {
        return ctx -> {
            List<ContextConfig> topicSeqConfigs = ctx.getContextMap().get(ConfigType.TOPIC_SEQ_CONFIG);
            if (!topicSeqConfigs.isEmpty()) topicSeqConfigs.remove(0);
        };
    }
}
