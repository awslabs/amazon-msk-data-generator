// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core.generator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultGen {
    private final boolean success;
    private final Map<List<String>, String> result;

    public ResultGen(boolean success, Map<List<String>, String> result) {
        this.success = success;
        this.result = result;
    }

    public ResultGen(boolean success) {
        this(success, null);
    }

    public boolean isSuccess() {
        return success && (this.result == null || result.values().stream().noneMatch(Objects::isNull));
    }

    public Map<List<String>, String> getResult() {
        return result;
    }
}
