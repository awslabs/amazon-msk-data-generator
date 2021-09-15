// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

class SchemaValueResultBuilderTest {
    @Test
    void buildSchemaValueResultBuilderWithEmptyKey() {
        Map<List<String>, String> inputMap =
                Collections.singletonMap(Collections.emptyList(), "b98e2d10-1dd1-4437-b641-ae45b99dd3b8");
        Pair<Schema, Object> result = SchemaValueResultBuilder.create(inputMap).build();

        assertNotNull(result);
        assertEquals(Schema.Type.STRING, result.getKey().type());
        assertEquals("b98e2d10-1dd1-4437-b641-ae45b99dd3b8", result.getValue());
    }

    @Test
    void buildSchemaValueResultBuilderWithNullKey() {
        Map<List<String>, String> inputMap = Collections.emptyMap();
        Pair<Schema, Object> result = SchemaValueResultBuilder.create(inputMap).build();

        assertNotNull(result);
        assertEquals(Schema.Type.STRING, result.getKey().type());
        assertEquals(null, result.getValue());
    }

    @Test
    void buildSchemaValueResultBuilderWithComplexKey() {
        List<String> creditCardNumberAttr = Collections.singletonList("creditCardNumber");
        List<String> fullName = new ArrayList<>(Arrays.asList("name", "full"));

        Map<List<String>, String> inputMap = new HashMap<List<String>, String>() {{
            put(creditCardNumberAttr, "6771-8969-1765-6253");
            put(fullName, "Dorothy Murazik");
        }};
        Pair<Schema, Object> result = SchemaValueResultBuilder.create(inputMap).build();

        assertNotNull(result);
        assertEquals(Schema.Type.STRUCT, result.getKey().type());
        assertEquals("com.amazonaws.mskdatagen.Gen0", result.getKey().name());

        assertNotNull(result.getKey().field("creditCardNumber"));
        assertNotNull(result.getKey().field("name"));

        Schema innerNameFields = result.getKey().field("name").schema();
        assertEquals(Schema.Type.STRUCT, innerNameFields.type());
        assertEquals("com.amazonaws.mskdatagen.Gen1", innerNameFields.name());
        assertNotNull(innerNameFields.field("full"));

        assertTrue(result.getValue() instanceof Struct);
        Struct value = (Struct) result.getValue();
        assertEquals(2, value.schema().fields().size());
        assertEquals("6771-8969-1765-6253", value.get("creditCardNumber"));
        assertTrue(value.get("name") instanceof Struct);
        Struct nameValue = (Struct) value.get("name");
        assertEquals("Dorothy Murazik", nameValue.get("full"));

        // Shouldn't be like separate parameter
        assertNull(result.getKey().field("full"));
    }
}
