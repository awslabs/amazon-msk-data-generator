// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Kind;

public class TopicParser implements Parser {
    @Override
    public Config parse(String[] tokens, String key, String value) {
        String[] configs = Arrays.copyOfRange(tokens, 2, tokens.length);

        return new ConfigBuilder().setKind(Kind.TOPIC)
                .setOriginalKey(key)
                .setTopic(tokens[1])
                .setConfig(Arrays.asList(configs))
                .setValue(value)
                .build();
    }
}
