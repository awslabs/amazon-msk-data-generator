// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.RetainedConfig;

public class ValidateShapeConflicts implements Validate {
    @Override
    public void validate(Context context) {
        List<GeneratorsConfigs> generatorsConfig = context.getGenerators().collect(Collectors.toList());

        for (GeneratorsConfigs generatorConfig : generatorsConfig) {
            checkConfig(context, generatorConfig, generatorConfig.getKeys(), "key");

            checkConfig(context, generatorConfig, generatorConfig.getValue(), "value");
        }
    }

    private void checkConfig(Context context, GeneratorsConfigs generatorConfig, Map<List<String>, List<Generator>> attrs, String ns) {
        if (attrs.containsKey(Collections.emptyList()) && attrs.size() > 1) {
            String message = "Both primitive and complex generator %s configurations were supplied for topic %s. Stopping because these configurations are incompatible. Either use a single %s primitive generator configuration or use one or more complex configurations.";
            String originalConfig = String.join("\n - ", getOriginalConfig(context, generatorConfig));
            throw new ValidateException(originalConfig, String.format(message, ns, generatorConfig.getTopic(), ns));
        }
    }

    private String[] getOriginalConfig(Context context, GeneratorsConfigs generatorConfig) {
        return context.getRetainedConfig()
                .filter(t -> ConfigType.GENERATORS_CONFIG.getConfigGroup().equals(t.getKind()))
                .map(RetainedConfig::getOriginalConfigs)
                .flatMap(Collection::stream)
                .filter(t -> "key".equals(t.getNs()) && generatorConfig.getTopic().equals(t.getTopic()))
                .map(config -> config.getOriginalKey() + " = " + config.getValue())
                .toArray(String[]::new);
    }
}
