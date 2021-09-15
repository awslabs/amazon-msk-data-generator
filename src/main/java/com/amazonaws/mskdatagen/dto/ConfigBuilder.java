// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto;

import java.util.List;

public class ConfigBuilder {
    private Kind kind;
    private String originalKey;
    private String topic;
    private String ns;
    private String attr;
    private boolean qualified;
    private String generator;
    private List<String> configs;
    private String value;

    public ConfigBuilder setKind(Kind kind) {
        this.kind = kind;
        return this;
    }

    public ConfigBuilder setOriginalKey(String originalKey) {
        this.originalKey = originalKey;
        return this;
    }

    public ConfigBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ConfigBuilder setNs(String ns) {
        this.ns = ns;
        return this;
    }

    public ConfigBuilder setAttr(String attr) {
        this.attr = attr;
        return this;
    }

    public ConfigBuilder setQualified(boolean qualified) {
        this.qualified = qualified;
        return this;
    }

    public ConfigBuilder setGenerator(String generator) {
        this.generator = generator;
        return this;
    }

    public ConfigBuilder setConfig(List<String> configs) {
        this.configs = configs;
        return this;
    }

    public ConfigBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public Config build() {
        return new Config(kind, originalKey, topic, ns, attr, qualified, generator, configs, value);
    }

}
