// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.List;
import java.util.Map;

public class GeneratedConfig implements ContextConfig {
    private boolean success;
    private String state;
    private String topic;
    private Map<List<String>, String> keyResults;
    private Map<List<String>, String> valResults;

    public GeneratedConfig(boolean success, String topic, Map<List<String>, String> keyResults, Map<List<String>, String> valResults) {
        this.success = success;
        this.topic = topic;
        this.keyResults = keyResults;
        this.valResults = valResults;
    }

    public GeneratedConfig(boolean success, String state) {
        this.success = success;
        this.state = state;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getState() {
        return state;
    }

    public Map<List<String>, String> getKeyResults() {
        return keyResults;
    }

    public Map<List<String>, String> getValResults() {
        return valResults;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }
}
