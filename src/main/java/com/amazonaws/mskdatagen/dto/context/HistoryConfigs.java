// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.generator.DepsParameters;

public class HistoryConfigs implements ContextConfig {
    private final String topic;
    private List<DepsParameters.NsKeyValue> history = new ArrayList<>();

    public HistoryConfigs(String topic) {
        this.topic = topic;
    }

    public List<DepsParameters.NsKeyValue> getHistory() {
        return history;
    }

    public void resizeHistoryMap(long size) {
        this.history = history.stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public String getTopic() {
        return this.topic;
    }
}
