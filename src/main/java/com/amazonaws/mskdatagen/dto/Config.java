// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Config {
    private final Kind kind;
    private final String originalKey;
    private final String topic;
    private final String ns;
    private final String attr;
    private final boolean qualified;
    private final String generator;
    private final List<String> configs;
    private final String value;

    public Config(Kind kind, String originalKey, String topic, String ns, String attr, boolean qualified, String generator,
                  List<String> configs, String value) {
        this.kind = kind;
        this.originalKey = originalKey;
        this.topic = topic;
        this.ns = ns;
        this.attr = attr;
        this.qualified = qualified;
        this.generator = generator;
        this.configs = configs;
        this.value = value;
    }

    public Kind getKind() {
        return kind;
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public String getTopic() {
        return topic;
    }

    public String getNs() {
        return ns;
    }

    public String getAttr() {
        return attr;
    }

    public List<String> getSplitAttr() {
        return Optional.ofNullable(attr)
                .map(t -> t.split("->"))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());  // Solo attr
    }

    public String getGenerator() {
        return generator;
    }

    public String getValue() {
        return value;
    }

    public boolean isQualified() {
        return qualified;
    }

    public List<String> getConfigs() {
        return configs;
    }

    @Override
    public String toString() {
        return "Config{" +
                "kind=" + kind +
                ", originalKey='" + originalKey + '\'' +
                ", topic='" + topic + '\'' +
                ", ns='" + ns + '\'' +
                ", attr='" + attr + '\'' +
                ", qualified=" + qualified +
                ", generator='" + generator + '\'' +
                ", configs=" + configs +
                ", value='" + value + '\'' +
                "}\n";
    }
}
