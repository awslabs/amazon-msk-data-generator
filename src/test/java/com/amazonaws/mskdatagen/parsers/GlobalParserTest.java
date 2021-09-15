// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GlobalParserTest {
    @Test
    void parse() {
        String key = "global.history.records.max";
        String[] split = key.split("\\.");

        Config parse = new GlobalParser().parse(split, key, "100000");

        Assertions.assertEquals(Kind.GLOBAL, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals(Arrays.asList("history", "records", "max"), parse.getConfigs());
        assertEquals("100000", parse.getValue());
    }
}
