// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.mskdatagen.dto.Config;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Kind;

class GenerateComplexParserTest {
    @Test
    void parseWithoutSometimesGenk() {
        String key = "genk.diets.catName.matching";
        String[] split = key.split("\\.");

        Config parse = new GenerateComplexParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.GENERATE_COMPLEX, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("diets", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertEquals("catName", parse.getAttr());
        assertFalse(parse.isQualified());
        assertEquals("matching", parse.getGenerator());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }

    @Test
    void parseWithoutSometimesGenv() {
        String key = "genv.diets.catName.matching";
        String[] split = key.split("\\.");

        Config parse = new GenerateComplexParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.GENERATE_COMPLEX, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("diets", parse.getTopic());
        assertEquals("value", parse.getNs());
        assertEquals("catName", parse.getAttr());
        assertFalse(parse.isQualified());
        assertEquals("matching", parse.getGenerator());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }

    @Test
    void parseWithSometimes() {
        String key = "genk.adopters.name.sometimes.with";
        String[] split = key.split("\\.");

        Config parse = new GenerateComplexParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.GENERATE_COMPLEX, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertEquals("name", parse.getAttr());
        assertTrue(parse.isQualified());
        assertEquals("with", parse.getGenerator());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }
}
