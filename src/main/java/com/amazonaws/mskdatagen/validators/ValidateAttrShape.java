// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.Kind;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.RetainedConfig;

public class ValidateAttrShape implements Validate {
    @Override
    public void validate(Context context) {
        List<Config> attrConfig = context.getRetainedConfig()
                .filter(t -> ConfigType.ATTR_CONFIG.getConfigGroup().equals(t.getKind()))
                .map(RetainedConfig::getOriginalConfigs)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (Config config : attrConfig) {
            getGen(context, config.getTopic(), config.getNs())
                    .ifPresent(gen -> {
                        if (gen.get(Collections.emptyList()) != null && Kind.ATTRIBUTE_PRIMITIVE != config.getKind()) {
                            String message = "Complex attribute configuration is supplied for topic %s, but its generated %s is a primitive type. Stopping because these configurations are incompatible. Either change its %s to a complex type or change this configuration to a primitive type.";
                            String originalConfig = config.getOriginalKey() + " = " + config.getValue();
                            throw new ValidateException(originalConfig, String.format(message, config.getTopic(), config.getNs(), config.getNs()));
                        }
                    });

            if (Kind.ATTRIBUTE_COMPLEX != config.getKind()) {
                String message = "Primitive attribute configuration is supplied for topic %s, but its generated %s is a complex type. Stopping because these configurations are incompatible. Either change its %s to a primitive type or change this configuration to a complex type.";
                String originalConfig = config.getOriginalKey() + " = " + config.getValue();
                throw new ValidateException(originalConfig, String.format(message, config.getTopic(), config.getNs(), config.getNs()));
            }
        }
    }

    private Optional<Map<List<String>, List<Generator>>> getGen(Context context, String topic, String ns) {
        return Optional.ofNullable(context.getContextMap().get(ConfigType.GENERATORS_CONFIG))
                .orElse(Collections.emptyList()).stream()
                .map(GeneratorsConfigs.class::cast)
                .filter(t -> topic.equals(t.getTopic()))
                .findFirst()
                .map(t -> t.getByNamespace(ns));
    }
}
