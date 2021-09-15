// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public enum ConfigType {
    FAKER_CONFIG("faker", Constants.GLOBAL),
    GLOBAL_CONFIG("global-configs", Constants.GLOBAL),
    RETAINED_CONFIG("configs-by-topic", Constants.GLOBAL),
    TOPIC_CONFIG("topic-configs", Constants.TOPIC),
    ATTR_CONFIG("attr-configs", Constants.ATTR),
    GENERATORS_CONFIG("generators", Constants.GEN),
    TIMESTAMPS_CONFIG("timestamps", Constants.GLOBAL),
    RECORDS_PRODUCED_CONFIG("records-produced", Constants.GLOBAL),
    RETIRED_TOPICS_CONFIG("retired-topics", Constants.GLOBAL),
    HISTORY_CONFIG("history", Constants.GLOBAL),
    TOPIC_SEQ_CONFIG("topic-seq", Constants.GLOBAL),
    GENERATED_CONFIG("generated", Constants.GLOBAL);

    private final String configName;
    private final String configGroup;

    ConfigType(String configName, String configGroup) {
        this.configName = configName;
        this.configGroup = configGroup;
    }

    public String getConfigName() {
        return configName;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    private static class Constants {
        public static final String GLOBAL = "global";
        public static final String TOPIC = "topic";
        public static final String ATTR = "attr";
        public static final String GEN = "gen";
    }
}
