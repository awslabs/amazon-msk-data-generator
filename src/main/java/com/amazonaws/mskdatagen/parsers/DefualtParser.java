// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;

public class DefualtParser implements Parser {
    @Override
    public Config parse(String[] tokens, String key, String value) {
        return new ConfigBuilder()
                .setOriginalKey(key)
                .setValue(value)
                .build();
    }
}
