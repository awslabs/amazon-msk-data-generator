// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Kind;
import com.amazonaws.mskdatagen.dto.Namespaces;

public class GenerateComplexParser implements Parser {
    @Override
    public Config parse(String[] tokens, String key, String value) {
        boolean isSometimes = tokens[3].equalsIgnoreCase("sometimes");

        return new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey(key)
                .setTopic(tokens[1])
                .setNs(Namespaces.getNamespace(tokens[0]))
                .setAttr(tokens[2])
                .setQualified(isSometimes)
                .setGenerator(isSometimes ? tokens[4] : tokens[3])
                .setValue(value)
                .build();
    }
}
