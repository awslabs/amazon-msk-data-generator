// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.amazonaws.mskdatagen.core.generator.ResultGen;
import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.core.generator.DepsParameters;

public class GeneratorsConfigs implements ContextConfig {
    private final String topic;
    private final Map<List<String>, List<Generator>> keys = new HashMap<>();
    private final Map<List<String>, List<Generator>> value = new HashMap<>();
    private final List<String> dependencies = new ArrayList<>();
    private Function<DepsParameters, ResultGen> keyF;
    private Function<DepsParameters, ResultGen> valueF;
    private long throttleNs;
    private Consumer<Context> retireFn;

    public GeneratorsConfigs(String topic) {
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public Map<List<String>, List<Generator>> getKeys() {
        return keys;
    }

    public Map<List<String>, List<Generator>> getValue() {
        return value;
    }

    public Function<DepsParameters, ResultGen> getKeyF() {
        return keyF;
    }

    public Function<DepsParameters, ResultGen> getValueF() {
        return valueF;
    }

    public void setKeyF(Function<DepsParameters, ResultGen> keyF) {
        this.keyF = keyF;
    }

    public void setValueF(Function<DepsParameters, ResultGen> valueF) {
        this.valueF = valueF;
    }

    public Map<List<String>, List<Generator>> getByNamespace(String ns) {
        if ("key".equals(ns)) {
            return getKeys();
        }
        return getValue();
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public long getThrottleNs() {
        return throttleNs;
    }

    public void setThrottleNs(long throttleNs) {
        this.throttleNs = throttleNs;
    }

    public Consumer<Context> getRetireFn() {
        return retireFn;
    }

    public void setRetireFn(Consumer<Context> retireFn) {
        this.retireFn = retireFn;
    }

    @Override
    public String toString() {
        return "GeneratorsConfigs{" +
                "topic='" + topic + '\'' +
                ", keys=" + keys +
                ", value=" + value +
                ", dependencies=" + dependencies +
                '}';
    }
}
