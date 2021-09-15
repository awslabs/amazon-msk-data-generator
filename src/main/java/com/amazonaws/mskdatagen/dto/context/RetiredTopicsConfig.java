// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class RetiredTopicsConfig implements ContextConfig {
    private final String topic;

    public RetiredTopicsConfig(String topic) {
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }
}
