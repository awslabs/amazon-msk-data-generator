// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.producer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

public class SchemaValueResultBuilder {
    private Map<List<String>, String> generatedResults;

    private SchemaValueResultBuilder(Map<List<String>, String> generatedResults) {
        this.generatedResults = generatedResults;
    }

    public static SchemaValueResultBuilder create(Map<List<String>, String> generatedResults) {
        return new SchemaValueResultBuilder(generatedResults);
    }

    public Pair<Schema, Object> build() {
        Schema schema = buildSchema();
        Object result = null;
        for (List<String> keyList : this.generatedResults.keySet()) {
            if (keyList.isEmpty()) {
                return Pair.of(schema, this.generatedResults.get(Collections.emptyList()));
            }

            Object currentResult = buildConvertedObjRec(keyList, schema, 0);
            if (result instanceof Struct && currentResult instanceof Struct) {
                Struct currentStuct = (Struct) currentResult;
                ((Struct) result).put(keyList.get(0), currentStuct.get(keyList.get(0)));
            } else {
                result = currentResult;
            }
        }

        return Pair.of(schema, result);
    }

    private Object buildConvertedObjRec(List<String> keyList, Schema schema, int depth) {
        if (schema.type() == Schema.Type.STRUCT) {
            Struct struct = new Struct(schema);
            String key = keyList.get(depth);
            Schema innerSchema = schema.field(key).schema();
            depth++;
            Object value = buildConvertedObjRec(keyList, innerSchema, depth);
            struct.put(key, value);
            return struct;
        }

        return this.generatedResults.get(keyList);
    }

    private Schema buildSchema() {
        if (this.generatedResults.isEmpty()) {
            return getPrimitiveSchema("");
        }

        SchemaBuilder builder = SchemaBuilder.struct().name("com.amazonaws.mskdatagen.Gen" + 0).optional();
        for (Map.Entry<List<String>, String> entry : this.generatedResults.entrySet()) {
            if (entry.getKey().isEmpty()) {
                return getPrimitiveSchema(this.generatedResults.get(entry.getKey()));
            }

            buildSchemaRec(entry.getKey(), builder, 0);
        }

        return builder.build();
    }

    private SchemaBuilder buildSchemaRec(List<String> keyList, SchemaBuilder builder, int depth) {
        if (depth < keyList.size() - 1) {
            String key = keyList.get(depth);

            depth++;
            Schema build = Objects.requireNonNull(buildSchemaRec(keyList, SchemaBuilder.struct().name("com.amazonaws.mskdatagen.Gen" + depth).optional(), depth))
                    .build();
            builder.field(key, build);
            return builder;
        }

        String key = keyList.get(depth);
        builder.field(key, buildSchemaPrimitive(this.generatedResults.get(keyList)));
        return builder;
    }

    private Schema buildSchemaPrimitive(Object value) {
        if (value != null) {
            return getPrimitiveSchema(value);
        }
        return Schema.OPTIONAL_STRING_SCHEMA;
    }

    private Schema getPrimitiveSchema(Object value) {
        if (value == null) {
            return Schema.OPTIONAL_BYTES_SCHEMA;
        } else if (value instanceof Integer) {
            return Schema.OPTIONAL_INT32_SCHEMA;
        } else if (value instanceof Long) {
            return Schema.OPTIONAL_INT64_SCHEMA;
        } else if (value instanceof Float) {
            return Schema.OPTIONAL_FLOAT32_SCHEMA;
        } else if (value instanceof Double) {
            return Schema.OPTIONAL_FLOAT64_SCHEMA;
        } else if (value instanceof Boolean) {
            return Schema.OPTIONAL_BOOLEAN_SCHEMA;
        }

        return Schema.OPTIONAL_STRING_SCHEMA;
    }
}
