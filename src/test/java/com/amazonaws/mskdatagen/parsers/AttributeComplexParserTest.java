// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttributeComplexParserTest {
    @Test
    void parseAttrk() {
        String key = "attrk.adopters.name.matching.rate";
        String[] split = key.split("\\.");

        Config parse = new AttributeComplexParser().parse(split, key, "0.05");

        Assertions.assertEquals(Kind.ATTRIBUTE_COMPLEX, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertEquals("name", parse.getAttr());
        assertEquals(Arrays.asList("matching", "rate"), parse.getConfigs());
        assertEquals("0.05", parse.getValue());
    }

    @Test
    void parseAttrv() {
        String key = "attrv.adopters.name.matching.rate";
        String[] split = key.split("\\.");

        Config parse = new AttributeComplexParser().parse(split, key, "0.05");

        assertEquals(Kind.ATTRIBUTE_COMPLEX, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals("value", parse.getNs());
        assertEquals("name", parse.getAttr());
        assertEquals(Arrays.asList("matching", "rate"), parse.getConfigs());
        assertEquals("0.05", parse.getValue());
    }
}
