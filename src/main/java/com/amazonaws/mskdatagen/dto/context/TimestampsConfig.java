// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.Map;

public class TimestampsConfig implements ContextConfig {
    private final Map<String, Long> timestamps;

    public TimestampsConfig(Map<String, Long> timestamps) {
        this.timestamps = timestamps;
    }

    public Map<String, Long> getTimestamps() {
        return timestamps;
    }

    @Override
    public String getTopic() {
        return null;
    }
}
