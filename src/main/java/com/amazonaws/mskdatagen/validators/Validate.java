// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import java.util.Collection;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.RetainedConfig;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;

public interface Validate {
    void validate(Context context);

    default String getOriginalConfig(Context context, ContextConfig conf, String configGroup) {
        return context.getRetainedConfig()
                .filter(t -> configGroup.equals(t.getKind()))
                .map(RetainedConfig::getOriginalConfigs)
                .flatMap(Collection::stream)
                .filter(t -> conf.getTopic().equals(t.getTopic()))
                .map(t -> t.getOriginalKey() + " = " + t.getValue())
                .findFirst().orElse(null);
    }
}
