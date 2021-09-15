// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import com.amazonaws.mskdatagen.dto.Config;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Kind;

class TopicParserTest {
    @Test
    void parse() {
        String key = "topic.adopters.tombstone.rate";
        String[] split = key.split("\\.");

        Config parse = new TopicParser().parse(split, key, "#{Internet.uuid}");

        assertEquals(Kind.TOPIC, parse.getKind());
        assertEquals(key, parse.getOriginalKey());
        assertEquals("adopters", parse.getTopic());
        assertEquals(Arrays.asList("tombstone", "rate"), parse.getConfigs());
        assertEquals("#{Internet.uuid}", parse.getValue());
    }
}
