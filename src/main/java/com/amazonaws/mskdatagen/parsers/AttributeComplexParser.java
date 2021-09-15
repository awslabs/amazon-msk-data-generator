// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.Namespaces;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Kind;

public class AttributeComplexParser implements Parser {
    @Override
    public Config parse(String[] tokens, String key, String value) {
        String[] configs = Arrays.copyOfRange(tokens, 3, tokens.length);

        return new ConfigBuilder().setKind(Kind.ATTRIBUTE_COMPLEX)
                .setOriginalKey(key)
                .setTopic(tokens[1])
                .setNs(Namespaces.getNamespace(tokens[0]))
                .setAttr(tokens[2])
                .setConfig(Arrays.asList(configs))
                .setValue(value)
                .build();
    }
}
