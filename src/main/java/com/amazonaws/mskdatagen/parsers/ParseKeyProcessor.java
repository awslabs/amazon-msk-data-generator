// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.Config;

public class ParseKeyProcessor {
    public static final String SEPARATOR = "\\.";

    public List<Config> parseKeys(Map<String, String> props) {
        return props.entrySet().stream()
                .map(this::parseKey)
                .collect(Collectors.toList());
    }

    private Config parseKey(Map.Entry<String, String> prop) {
        String key = prop.getKey();
        String[] tokens = key.split(SEPARATOR);

        Parser parser = getParser(tokens[0]);
        return parser.parse(tokens, key, prop.getValue());
    }

    private Parser getParser(String token) {
        Parser parser;
        switch (token) {
            case "genkp":
            case "genvp":
                parser = new GeneratePrimitiveParser();
                break;
            case "genk":
            case "genv":
                parser = new GenerateComplexParser();
                break;
            case "attrkp":
            case "attrvp":
                parser = new AttributePrimitiveParser();
                break;
            case "attrk":
            case "attrv":
                parser = new AttributeComplexParser();
                break;
            case "topic":
                parser = new TopicParser();
                break;
            case "global":
                parser = new GlobalParser();
                break;
            default:
                parser = new DefualtParser();
        }

        return parser;
    }
}
