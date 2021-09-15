// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core.generator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DepsParameters {
    private final List<String> deps;
    private final Map<String, NsKeyValue> depTargets;

    public DepsParameters(List<String> deps, Map<String, NsKeyValue> depTargets) {
        this.deps = deps;
        this.depTargets = depTargets;
    }

    public List<String> getDeps() {
        return deps;
    }

    public Map<String, NsKeyValue> getDepTargets() {
        return Optional.ofNullable(depTargets).orElse(Collections.emptyMap());
    }

    public static class NsKeyValue {
        private final Map<List<String>, String> key;
        private final Map<List<String>, String> value;

        public NsKeyValue(Map<List<String>, String> key, Map<List<String>, String> value) {
            this.key = key;
            this.value = value;
        }

        public Map<List<String>, String> getKey() {
            return Optional.ofNullable(key).orElse(Collections.emptyMap());
        }

        public Map<List<String>, String> getValue() {
            return Optional.ofNullable(value).orElse(Collections.emptyMap());
        }

        public Map<List<String>, String> getKeyOrValueByNs(String ns) {
            return "key".equals(ns) ? getKey() : getValue();
        }
    }
}
