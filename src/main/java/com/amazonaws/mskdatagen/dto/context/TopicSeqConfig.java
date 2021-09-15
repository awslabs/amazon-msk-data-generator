// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class TopicSeqConfig implements ContextConfig {
    private final String topicsSeq;

    public TopicSeqConfig(String topicsSeq) {
        this.topicsSeq = topicsSeq;
    }

    @Override
    public String getTopic() {
        return this.topicsSeq;
    }
}
