// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;

public class ValidateTopicConfigs implements Validate {
    public static final String MESSAGE = "Topic configuration is supplied for topic(s) %s, but no generators are specified for them. Stopping because these topic configurations don't do anything. Either add generators for these topics or remove these topic configurations.";

    @Override
    public void validate(Context context) {
        List<String> generatorsTopicsConfig
                = context.getGenerators().map(ContextConfig::getTopic).collect(Collectors.toList());

        context.getTopicConfigs().filter(t -> !generatorsTopicsConfig.contains(t.getTopic())).findFirst()
                .ifPresent(conf -> {
                    String originalConfig = getOriginalConfig(context, conf, ConfigType.TOPIC_CONFIG.getConfigGroup());
                    throw new ValidateException(originalConfig, String.format(MESSAGE, conf.getTopic()));
                });
    }
}
