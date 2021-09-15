// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.context.Strategy;
import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;

public class ValidateDependencies implements Validate {
    @Override
    public void validate(Context context) {
        List<GeneratorsConfigs> generatorsConfigs = context.getGenerators().collect(Collectors.toList());

        List<String> topics = generatorsConfigs.stream().map(GeneratorsConfigs::getTopic).collect(Collectors.toList());
        for (GeneratorsConfigs generatorsConfig : generatorsConfigs) {
            if (!generatorsConfig.getDependencies().isEmpty()) {
                for (Generator generator : getGeneratorsByNs(generatorsConfig.getKeys())) {
                    validateDependencyGenerator(topics, generatorsConfig.getTopic(), generator, generatorsConfigs);
                }

                for (Generator generator : getGeneratorsByNs(generatorsConfig.getValue())) {
                    validateDependencyGenerator(topics, generatorsConfig.getTopic(), generator, generatorsConfigs);
                }
            }
        }
    }

    private List<Generator> getGeneratorsByNs(Map<List<String>, List<Generator>> generatorsConfig) {
        return generatorsConfig.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void validateDependencyGenerator(List<String> topics, String topic, Generator generator, List<GeneratorsConfigs> generatorsConfigs) {
        if (generator.getStrategy() == Strategy.DEPENDENT) {
            String depTopic = generator.getTopic();
            String ns = generator.getNs();

            if (!topics.contains(depTopic)) {
                String message = "Found a generator for topic %s that is dependent on topic %s, but no generator is defined for %s. Stopping because no data can ever be produced for topic %s. Either define a generator for topic %s or remove this generator.";
                String originalConfig = generator.getOriginalKey() + " = " + generator.getOriginalValue();
                throw new ValidateException(originalConfig, String.format(message, topic, depTopic, depTopic, topic, depTopic));
            }

            if (generator.getAttr() != null && !generator.getAttr().isEmpty()
                    && isNotAttrPresent(generator.getAttr(), generatorsConfigs, depTopic, ns)) {
                String message = "Found a generator for topic %s that is dependent on attribute %s in topic %s's %s, but no generator is defined for that attribute. Stopping because this generator would always return null. Either define a generator for the attribute or remove this generator.";
                String formattedAttr = String.join("->", generator.getAttr().toArray(new String[0]));
                String originalConfig = generator.getOriginalKey() + " = " + generator.getOriginalValue();
                throw new ValidateException(originalConfig, String.format(message, topic, formattedAttr, depTopic, ns));
            } else if ((generator.getAttr() == null || generator.getAttr().isEmpty())
                    && isNotAttrPresent(Collections.emptyList(), generatorsConfigs, depTopic, ns)) {
                String message = "Found a generator for topic %s that is dependent on topic %s's %s, but there's no primitive generator defined for the %s. Stopping because this generator is incompatible. Either define a generator for the %s or remove this dependency.";
                String originalConfig = generator.getOriginalKey() + " = " + generator.getOriginalValue();
                throw new ValidateException(originalConfig, String.format(message, topic, depTopic, ns, ns, ns));
            }
        }
    }

    private boolean isNotAttrPresent(List<String> attr, List<GeneratorsConfigs> generatorsConfigs, String depTopic, String ns) {
        return generatorsConfigs.stream()
                .filter(t -> depTopic.equals(t.getTopic()))
                .map(t -> t.getByNamespace(ns))
                .noneMatch(t -> t.containsKey(attr));
    }
}
