// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mskdatagen.dto.Config;

public class RetainedConfig implements ContextConfig {
    private final String configGroup;
    private final List<Config> originalConfigs = new ArrayList<>();

    public RetainedConfig(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getKind() {
        return configGroup;
    }

    public List<Config> getOriginalConfigs() {
        return originalConfigs;
    }

    public void addOriginalConfigs(Config originalConfigs) {
        this.originalConfigs.add(originalConfigs);
    }

    @Override
    public String toString() {
        return "RetainedConfig{" +
                "kind=" + configGroup +
                ", originalConfigs=" + originalConfigs +
                '}';
    }

    @Override
    public String getTopic() {
        return "";
    }
}
