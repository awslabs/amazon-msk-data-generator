// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GeneratePrimitiveParserTest {
    @Test
    void parseWithoutSometimesGenkp() {
        String key = "genkp.owners.with";
        String[] split = key.split("\\.");

        Config parse = new GeneratePrimitiveParser().parse(split, key, "#{Internet.uuid}");

        Assertions.assertEquals(Kind.GENERATE_PRIMITIVE, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("owners", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertFalse(parse.isQualified());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }

    @Test
    void parseWithoutSometimesGenvp() {
        String key = "genvp.owners.with";
        String[] split = key.split("\\.");

        Config parse = new GeneratePrimitiveParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.GENERATE_PRIMITIVE, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("owners", parse.getTopic());
        assertEquals("value", parse.getNs());
        assertFalse(parse.isQualified());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }

    @Test
    void parseWithSometimes() {
        String key = "genkp.owners.sometimes.with";
        String[] split = key.split("\\.");

        Config parse = new GeneratePrimitiveParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.GENERATE_PRIMITIVE, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("owners", parse.getTopic());
        assertEquals("key", parse.getNs());
        assertTrue(parse.isQualified());
        assertEquals("with", parse.getGenerator());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }
}
