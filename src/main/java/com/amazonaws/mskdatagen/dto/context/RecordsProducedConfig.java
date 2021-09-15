// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.Map;

public class RecordsProducedConfig implements ContextConfig {
    private final Map<String, Long> counters;

    public RecordsProducedConfig(Map<String, Long> counters) {
        this.counters = counters;
    }

    public Map<String, Long> getCounters() {
        return counters;
    }

    @Override
    public String getTopic() {
        return null;
    }
}

