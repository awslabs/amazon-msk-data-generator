// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.context.GeneratedConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateSourceRecord {
    private static final Logger log = LoggerFactory.getLogger(GenerateSourceRecord.class);

    private final Context context;

    public GenerateSourceRecord(Context context) {
        this.context = context;
    }

    public List<SourceRecord> generateSourceRecord() {
        new ApplyGenerator(context).advanceUntilSuccess();

        GeneratedConfig generated = context.getGenerated();

        if (generated.isSuccess()) {
            List<SourceRecord> records = new ArrayList<>();
            String topic = generated.getTopic();

            Map<List<String>, String> keyResults = generated.getKeyResults();
            Pair<Schema, Object> keySchemaValueResultBuilder = SchemaValueResultBuilder.create(keyResults)
                    .build();

            Map<List<String>, String> valueResults = generated.getValResults();
            Pair<Schema, Object> valueSchemaValueResultBuilder = SchemaValueResultBuilder.create(valueResults)
                    .build();

            records
                    .add(new SourceRecord(Collections.emptyMap(), Collections.emptyMap(), topic, null,
                            keySchemaValueResultBuilder.getKey(), keySchemaValueResultBuilder.getValue(),
                            valueSchemaValueResultBuilder.getKey(), valueSchemaValueResultBuilder.getValue()));

            return records;
        } else if ("drained".equals(generated.getState())) {
            log.debug("Generation result is empty.");
            return Collections.emptyList();
        }

        log.error("State machine returned an unusable status: {}", generated.getState());
        throw new IllegalStateException("State machine returned an unusable status: " + generated.getState());
    }
}
