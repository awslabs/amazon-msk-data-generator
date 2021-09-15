// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.RetainedConfig;
import com.amazonaws.mskdatagen.dto.Kind;

public class ValidateUnusedAttrs implements Validate {
    @Override
    public void validate(Context context) {
        List<Config> attrConfig = context.getRetainedConfig()
                .filter(t -> ConfigType.ATTR_CONFIG.getConfigGroup().equals(t.getKind()))
                .map(RetainedConfig::getOriginalConfigs)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (Config config : attrConfig) {
            if (config.getKind() == Kind.ATTRIBUTE_COMPLEX && !isGenExists(context, config)) {
                String message = "Complex attribute configuration is supplied for topic %s, but there is no generator that creates this attribute. Stopping because this configuration does nothing. Either add a generator for this attribute or remove this configuration.";
                String originalConfig = config.getOriginalKey() + " = " + config.getValue();
                throw new ValidateException(originalConfig, String.format(message, config.getTopic()));
            }
        }
    }

    private boolean isGenExists(Context context, Config config) {
        Optional<GeneratorsConfigs> generatorsConfigs = Optional.ofNullable(context.getContextMap().get(ConfigType.GENERATORS_CONFIG))
                .orElse(Collections.emptyList()).stream()
                .map(GeneratorsConfigs.class::cast)
                .filter(t -> config.getTopic().equals(t.getTopic()))
                .findFirst();

        if (generatorsConfigs.isPresent()) {
            Set<List<String>> attrKey = generatorsConfigs
                    .map(t -> t.getByNamespace(config.getNs()))
                    .map(Map::keySet)
                    .orElse(Collections.emptySet());

            List<String> attrs = attrKey.stream().map(t -> t.toArray(new String[0]))
                    .map(t -> String.join("->", t))
                    .collect(Collectors.toList());

            return attrs.stream().anyMatch(t -> config.getAttr().equals(t));
        }

        return false;
    }
}
