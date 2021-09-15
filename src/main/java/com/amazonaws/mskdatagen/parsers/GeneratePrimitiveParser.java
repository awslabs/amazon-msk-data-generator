// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Namespaces;
import com.amazonaws.mskdatagen.dto.Kind;

public class GeneratePrimitiveParser implements Parser {
    @Override
    public Config parse(String[] tokens, String key, String value) {
        boolean isSometimes = tokens[2].equalsIgnoreCase("sometimes");

        return new ConfigBuilder().setKind(Kind.GENERATE_PRIMITIVE)
                .setOriginalKey(key)
                .setTopic(tokens[1])
                .setNs(Namespaces.getNamespace(tokens[0]))
                .setQualified(isSometimes)
                .setGenerator(isSometimes ? tokens[3] : tokens[2])
                .setValue(value)
                .build();
    }
}
