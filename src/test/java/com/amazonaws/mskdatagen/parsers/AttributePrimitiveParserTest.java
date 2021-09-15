// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Kind;

class AttributePrimitiveParserTest {
    @Test
    void parseAttrkp() {
        String key = "attrkp.adopters.matching.rate";
        String[] split = key.split("\\.");

        Config parse = new AttributePrimitiveParser().parse(split, key, "0.05");

        assertEquals(Kind.ATTRIBUTE_PRIMITIVE, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertEquals(Arrays.asList("matching", "rate"), parse.getConfigs());
        assertEquals("0.05", parse.getValue());
    }

    @Test
    void parseAttrvp() {
        String key = "attrvp.adopters.matching.rate";
        String[] split = key.split("\\.");

        Config parse = new AttributePrimitiveParser().parse(split, key, "0.05");

        assertEquals(Kind.ATTRIBUTE_PRIMITIVE, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals("value", parse.getNs());
        assertEquals(Arrays.asList("matching", "rate"), parse.getConfigs());
        assertEquals("0.05", parse.getValue());
    }
}
