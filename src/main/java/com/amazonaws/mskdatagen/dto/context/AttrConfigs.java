// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class AttrConfigs implements ContextConfig {
    private final String topic;
    private final NsMap nsMap = new NsMap();

    public AttrConfigs(String topic) {
        this.topic = topic;
    }

    public NsMap getNsMap() {
        return nsMap;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return "\nAttrConfigs{" +
                "topic='" + topic + '\'' +
                ", nsMap=" + nsMap +
                "} \n";
    }
}
