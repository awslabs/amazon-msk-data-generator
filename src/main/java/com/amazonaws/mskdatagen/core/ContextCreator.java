// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0.mskgen.core;

package com.amazonaws.mskdatagen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.RecordsProducedConfig;
import com.amazonaws.mskdatagen.dto.context.TimestampsConfig;
import com.amazonaws.mskdatagen.dto.context.TopicConfigs;
import com.amazonaws.mskdatagen.parsers.ParseKeyProcessor;
import com.amazonaws.mskdatagen.validators.Validate;
import com.amazonaws.mskdatagen.validators.ValidateAttrConfigs;
import com.amazonaws.mskdatagen.validators.ValidateAttrShape;
import com.amazonaws.mskdatagen.validators.ValidateDependencies;
import com.amazonaws.mskdatagen.validators.ValidateShapeConflicts;
import com.amazonaws.mskdatagen.validators.ValidateTopicConfigs;
import com.amazonaws.mskdatagen.validators.ValidateUnusedAttrs;

public class ContextCreator {
    public static final long DEFAULT_THROTTLE_MS = 0;

    public Context makeContext(Map<String, String> props) {
        List<Config> configs = new ParseKeyProcessor().parseKeys(props);
        Context context = new Context(configs);

        validateContext(context);
        launchContextInitializer(context);

        return context;
    }

    private void validateContext(Context context) {
        List<Validate> validators = new ArrayList<>();
        validators.add(new ValidateTopicConfigs());
        validators.add(new ValidateAttrConfigs());
        validators.add(new ValidateAttrShape());
        validators.add(new ValidateUnusedAttrs());
        validators.add(new ValidateShapeConflicts());
        validators.add(new ValidateDependencies());

        for (Validate validator : validators) {
            validator.validate(context);
        }
    }

    private void launchContextInitializer(Context context) {
        List<ContextInitializer> contextInitializers = new ArrayList<>();
        contextInitializers.add(new CompileGeneratorStrategies(context));
        contextInitializers.add(new TopicSequenceGenerator(context));
        contextInitializers.add(addInitializerTopicTimestamps(context));
        contextInitializers.add(addThrottleDurations(context));
        contextInitializers.add(addEventCounters(context));
        contextInitializers.add(new CompileRetireStrategy(context));

        contextInitializers.forEach(ContextInitializer::prepare);
    }

    private ContextInitializer addEventCounters(Context context) {
        return () -> {
            Map<String, Long> counters = context.getContextMap().get(ConfigType.GENERATORS_CONFIG)
                    .stream().map(GeneratorsConfigs.class::cast)
                    .collect(Collectors.toMap(GeneratorsConfigs::getTopic, t -> (long) 0));

            ContextConfig recordsProducedConfig = new RecordsProducedConfig(counters);
            context.getContextMap().put(ConfigType.RECORDS_PRODUCED_CONFIG, Collections.singletonList(recordsProducedConfig));
        };
    }

    private ContextInitializer addThrottleDurations(Context context) {
        return () -> {
            List<GeneratorsConfigs> generatorsConfigs = context.getContextMap().get(ConfigType.GENERATORS_CONFIG)
                    .stream().map(GeneratorsConfigs.class::cast).collect(Collectors.toList());

            for (GeneratorsConfigs generatorsConfig : generatorsConfigs) {
                Long throttleMS = context.getTopicConfigs()
                        .filter(t -> generatorsConfig.getTopic().equals(t.getTopic())).map(TopicConfigs::getThrottleMs)
                        .findFirst().orElseGet(() -> Optional.ofNullable(context.getGlobalConfig().getThrottleMs())
                                .orElse(DEFAULT_THROTTLE_MS));

                generatorsConfig.setThrottleNs(1000000 * throttleMS);
            }
        };
    }

    private ContextInitializer addInitializerTopicTimestamps(Context context) {
        return () -> {
            Map<String, Long> timestamps = context.getGenerators()
                    .collect(Collectors.toMap(GeneratorsConfigs::getTopic, t -> Long.MIN_VALUE));

            context.getContextMap()
                    .put(ConfigType.TIMESTAMPS_CONFIG, Collections.singletonList(new TimestampsConfig(timestamps)));
        };
    }
}
