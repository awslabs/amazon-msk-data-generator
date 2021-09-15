// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;

public class GeneratorConnectorTaskTest {

    @Test
    void getVersionTest() {
        GeneratorConnectorTask GeneratorConnectorTask = new GeneratorConnectorTask();

        assertEquals("v0.0", GeneratorConnectorTask.version());
    }

    @Test
    void testGenerateSourceRecordsWithRefs() {
        GeneratorConnectorTask GeneratorConnectorTask = new GeneratorConnectorTask();
        GeneratorConnectorTask.start(getPropsWithRef());

        List<SourceRecord> result = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            List<SourceRecord> oneListOfRecords = GeneratorConnectorTask.poll();
            result.addAll(oneListOfRecords);

            System.out.println(oneListOfRecords);

            if (oneListOfRecords.size() > 0) {
                assertEquals(1, oneListOfRecords.size());
                SourceRecord sourceRecord = oneListOfRecords.get(0);
                assertEquals(0, sourceRecord.sourcePartition().size());
                assertEquals(0, sourceRecord.sourceOffset().size());

                assertNotNull(sourceRecord.topic());
                assertNull(sourceRecord.kafkaPartition());
                assertNull(sourceRecord.timestamp());
                assertEquals(0, sourceRecord.headers().size());

                boolean assertResult = false;
                assertResult = assertIfCustomerWithEmptyKeyTopic(sourceRecord, assertResult);
                assertResult = assertIfOrderWithTrackingAndProductIdTopic(sourceRecord, assertResult);

                assertTrue(assertResult, "Source can't be checked " + oneListOfRecords.get(0));
            }
        }

        assertTrue(result.size() > 95, "Real result size is " + result.size());
        assertEquals(2, result.stream().map(ConnectRecord::topic).distinct().count());
    }

    @Test
    void testGenerateSourceRecordsWithFullConfig() {
        GeneratorConnectorTask GeneratorConnectorTask = new GeneratorConnectorTask();
        GeneratorConnectorTask.start(getPropsWithTopicConfig());

        List<SourceRecord> result = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            List<SourceRecord> oneListOfRecords = GeneratorConnectorTask.poll();
            result.addAll(oneListOfRecords);

            if (oneListOfRecords.size() > 0) {
                assertEquals(1, oneListOfRecords.size());
                SourceRecord sourceRecord = oneListOfRecords.get(0);
                assertEquals(0, sourceRecord.sourcePartition().size());
                assertEquals(0, sourceRecord.sourceOffset().size());

                assertNotNull(sourceRecord.topic());
                assertNull(sourceRecord.kafkaPartition());
                assertNull(sourceRecord.timestamp());
                assertEquals(0, sourceRecord.headers().size());

                boolean assertResult = false;
                assertResult = assertIfOwnersTopic(sourceRecord, assertResult);
                assertResult = assertIfAdoptersTopic(sourceRecord, assertResult);
                assertResult = assertIfCatsTopic(sourceRecord, assertResult);
                assertResult = assertIfDietsTopic(sourceRecord, assertResult);

                assertTrue(assertResult, "Source can't be checked " + oneListOfRecords.get(0));
            }
        }

        assertTrue(result.size() > 65, "Real result size is " + result.size());
        assertEquals(4, result.stream().map(ConnectRecord::topic).distinct().count());
    }

    @Test
    void testGenerateSourceRecordsWithShortConfig() {
        GeneratorConnectorTask GeneratorConnectorTask = new GeneratorConnectorTask();
        GeneratorConnectorTask.start(getShortProps());

        List<SourceRecord> result = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            List<SourceRecord> oneListOfRecords = GeneratorConnectorTask.poll();
            result.addAll(oneListOfRecords);

            assertEquals(1, oneListOfRecords.size());
            SourceRecord sourceRecord = oneListOfRecords.get(0);
            assertEquals(0, sourceRecord.sourcePartition().size());
            assertEquals(0, sourceRecord.sourceOffset().size());

            assertNotNull(sourceRecord.topic());
            assertNull(sourceRecord.kafkaPartition());
            assertNull(sourceRecord.timestamp());
            assertEquals(0, sourceRecord.headers().size());

            boolean assertResult = false;
            assertResult = assertIfInventoryTopic(sourceRecord, assertResult);
            assertResult = assertIfCustomerTopic(sourceRecord, assertResult);
            assertResult = assertIfOrderTopic(sourceRecord, assertResult);

            assertTrue(assertResult, "Source can't be checked " + oneListOfRecords.get(0));
        }

        assertEquals(100, result.size());
        assertEquals(3, result.stream().map(ConnectRecord::topic).distinct().count());
    }

    private boolean assertIfOwnersTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("owners".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            assertNotNull(UUID.fromString((String) sourceRecord.key()));
            assertEquals(Schema.STRING_SCHEMA.type(), sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertNotNull(value.get("creditCardNumber"));
            assertNotNull(value.get("name"));
            Struct name = (Struct) value.get("name");
            assertEquals("com.amazonaws.mskdatagen.Gen1", name.schema().name());
            assertNotNull(name.get("full"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfAdoptersTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("adopters".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            Struct name = (Struct) sourceRecord.key();
            assertNotNull(name.get("name"));
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.keySchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertNotNull(value.get("jobTitle"));
            assertNotNull(value.get("age"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfCatsTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("cats".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            Struct name = (Struct) sourceRecord.key();
            assertNotNull(name.get("name"));
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.keySchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertNotNull(UUID.fromString((String) value.get("owner")));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfDietsTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("diets".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            Struct name = (Struct) sourceRecord.key();
            assertNotNull(name.get("catName"));
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.keySchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertNotNull(value.get("size"));
            assertNotNull(value.get("dish"));
            assertNotNull(value.get("measurement"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfInventoryTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("inventory".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            assertEquals(Schema.STRING_SCHEMA.type(), sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertEquals(3, value.schema().fields().size());
            assertNotNull(value.get("amount_in_stock"));
            assertNotNull(value.get("last_updated"));
            assertNotNull(value.get("product_name"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfOrderTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("order".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            assertEquals(Schema.STRING_SCHEMA.type(), sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertEquals(2, value.schema().fields().size());
            assertNotNull(value.get("quantity"));
            assertNotNull(value.get("customer_id"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfCustomerTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("customer".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            assertEquals(Schema.STRING_SCHEMA.type(), sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertEquals(4, value.schema().fields().size());
            assertNotNull(value.get("favorite_beer"));
            assertNotNull(value.get("gender"));
            assertNotNull(value.get("name"));
            assertNotNull(value.get("state"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfCustomerWithEmptyKeyTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("customer".equals(sourceRecord.topic())) {
            assertNull(sourceRecord.key());
            assertEquals(Schema.Type.STRING, sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertEquals(5, value.schema().fields().size());
            assertNotNull(value.get("favorite_beer"));
            assertNotNull(value.get("gender"));
            assertNotNull(value.get("name"));
            assertNotNull(value.get("state"));
            assertNotNull(value.get("customer_id"));

            return true;
        }

        return assertResult;
    }

    private boolean assertIfOrderWithTrackingAndProductIdTopic(SourceRecord sourceRecord, boolean assertResult) {
        if ("order".equals(sourceRecord.topic())) {
            assertNotNull(sourceRecord.key());
            assertEquals(Schema.STRING_SCHEMA.type(), sourceRecord.keySchema().type());

            assertNotNull(sourceRecord.value());
            assertEquals("com.amazonaws.mskdatagen.Gen0", sourceRecord.valueSchema().name());
            assertEquals(Schema.Type.STRUCT, sourceRecord.valueSchema().type());
            Struct value = (Struct) sourceRecord.value();
            assertEquals(4, value.schema().fields().size());
            assertNotNull(value.get("quantity"));
            assertNotNull(value.get("customer_id"));
            assertNotNull(value.get("order_tracking_id"));
            assertNotNull(value.get("product_id"));

            return true;
        }

        return assertResult;
    }

    private Map<String, String> getPropsWithTopicConfig() {
        return new HashMap<String, String>() {{
            put("name", "msk-datagen-source");
            put("connector.class", "GeneratorSourceConnector");

            put("genkp.owners.with", "#{Internet.uuid}");
            put("genv.owners.name->full.with", "#{Name.full_name}");
            put("genv.owners.creditCardNumber.with", "#{Finance.credit_card}");

            put("genk.cats.name.with", "#{FunnyName.name}");
            put("genv.cats.owner.matching", "owners.key");

            put("genk.diets.catName.matching", "cats.key.name");
            put("genv.diets.dish.with", "#{Food.vegetables}");
            put("genv.diets.measurement.with", "#{Food.measurements}");
            put("genv.diets.size.with", "#{Food.measurement_sizes}");
            //			put("attrk.diets.second.null.rate", "0.05");
            //			put("genk.diets.second.with", "#{Second.Key}");

            put("genk.adopters.name.sometimes.with", "#{Name.full_name}");
            put("genk.adopters.name.sometimes.matching", "adopters.key.name");
            put("genv.adopters.jobTitle.with", "#{Job.title}");
            put("genv.adopters.age.with", "#{Number.randomDigit}");

            put("attrk.adopters.name.matching.rate", "0.05");

            put("topic.adopters.tombstone.rate", "0.10");
            put("topic.adopters.records.exactly", "10");

            //			put("global.throttle.ms","500");
            put("global.history.records.max", "100000");
        }};
    }

    private Map<String, String> getPropsWithRef() {
        return new HashMap<String, String>() {{
            put("name", "msk-datagen-source");
            put("connector.class", "GeneratorSourceConnector");

            put("genv.customer.name.with", "#{Name.full_name}");
            put("genv.customer.gender.with", "#{Demographic.sex}");
            put("genv.customer.favorite_beer.with", "#{Beer.name}");
            put("genv.customer.state.with", "#{Address.state}");
            put("genv.customer.customer_id.with", "#{Code.isbn10}");

            put("genkp.order.with", "#{Code.isbn10}");
            put("genv.order.product_id.with", "#{number.number_between '101','109'}");
            put("genv.order.quantity.with", "#{number.number_between '1','5'}");
            put("genv.order.customer_id.matching", "customer.value.customer_id");
            put("genv.order.order_tracking_id.with", "#{Lorem.characters '15'}");

            put("global.history.records.max", "100000");
        }};
    }

    private Map<String, String> getPropsWithSelfRef() {
        return new HashMap<String, String>() {{
            put("name", "msk-datagen-source");
            put("connector.class", "GeneratorSourceConnector");

            put("genkp.customer.with", "#{Code.isbn10}");
            put("genv.customer.name.with", "#{Name.full_name}");
            put("genv.customer.gender.with", "#{Demographic.sex}");
            put("genv.customer.favorite_beer.with", "#{Beer.name}");
            put("genv.customer.state.with", "#{Address.state}");
            put("genv.customer.customer_id.matching", "customer.key");
        }};
    }

    private Map<String, String> getShortProps() {
        return new HashMap<String, String>() {{
            put("name", "msk-datagen-source");
            put("connector.class", "GeneratorSourceConnector");

            put("genkp.inventory.sometimes.with", "#{Code.asin}");
            put("genkp.inventory.sometimes.matching", "inventory.key");
            put("genv.inventory.amount_in_stock.with", "#{number.number_between '5','15'}");
            put("genv.inventory.product_name.with", "#{Commerce.product_name}");
            put("genv.inventory.last_updated.with", "#{date.past '10','SECONDS'}");

            put("genkp.customer.with", "#{Code.isbn10}");
            put("genv.customer.name.with", "#{Name.full_name}");
            put("genv.customer.gender.with", "#{Demographic.sex}");
            put("genv.customer.favorite_beer.with", "#{Beer.name}");
            put("genv.customer.state.with", "#{Address.state}");

            put("genkp.order.matching", "inventory.key");
            put("genv.order.quantity.with", "#{number.number_between '1','5'}");
            put("genv.order.customer_id.matching", "customer.key");

            put("global.throttle.ms", "100");
            put("global.history.records.max", "10000");
        }};
    }
}
