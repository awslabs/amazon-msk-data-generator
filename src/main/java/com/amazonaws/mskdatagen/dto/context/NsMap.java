// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.HashMap;
import java.util.Map;

public class NsMap {
    private final Map<String, AttrConfig> key = new HashMap<>();
    private final Map<String, AttrConfig> value = new HashMap<>();

    public Map<String, AttrConfig> getKey() {
        return key;
    }

    public Map<String, AttrConfig> getValue() {
        return value;
    }

    public Map<String, AttrConfig> getMapByNs(String ns) {
        if ("key".equals(ns)) {
            return getKey();
        } else if ("value".equals(ns)) {
            return getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return "NsMap{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
