// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core;

import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Namespaces;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Test;

class TopicSequenceGeneratorTest {
    @Test
    void testGenerateTopics() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genk.owners.test.with")
                .setTopic("owners")
                .setNs(Namespaces.getNamespace("genk"))
                .setAttr("test")
                .setGenerator("with")
                .setValue("2")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genk.cat.test.with")
                .setTopic("cat")
                .setNs(Namespaces.getNamespace("genk"))
                .setAttr("test")
                .setGenerator("with")
                .setValue("5")
                .build());

        Context context = new Context(props);
        TopicSequenceGenerator topicSequenceGenerator = new TopicSequenceGenerator(context);
        topicSequenceGenerator.prepare();

        assertTimeout(Duration.ofMillis(100), () -> {
            assertTrue(context.getTopicSeq().count() > 5);
        });
    }

//	@Test
//	void testGenerateTopicsWithThreadSafeAction() throws ExecutionException, InterruptedException
//	{
//		List<Config> props = new ArrayList<>();
//
//		props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
//			.setOriginalKey("genk.owners.test.with")
//			.setTopic("owners")
//			.setNs(Namespaces.getNamespace("genk"))
//			.setAttr("test")
//			.setGenerator("with")
//			.setValue("2")
//			.build());
//
//		props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
//			.setOriginalKey("genk.cat.test.with")
//			.setTopic("cat")
//			.setNs(Namespaces.getNamespace("genk"))
//			.setAttr("test")
//			.setGenerator("with")
//			.setValue("5")
//			.build());
//
//		Context context = new Context(props);
//		TopicSequenceGenerator topicSequenceGenerator = new TopicSequenceGenerator(context);
//		topicSequenceGenerator.prepare();
//
//		pauseSeconds(1);
//		CompletableFuture<Boolean> booleanCompletableFuture = CompletableFuture.supplyAsync(() -> {
//			assertTrue(context.getTopicSeq().count() > 50);
//			context.getContextMap().get(ConfigType.TOPIC_SEQ_CONFIG).clear();
//			assertTrue(context.getTopicSeq().count() < 50);
//			pauseSeconds(1);
//			assertTrue(context.getTopicSeq().count() > 50);
//			return true;
//		});
//
//		pauseSeconds(2);
//		assertTrue(booleanCompletableFuture.get());
//
//		assertTimeout(Duration.ofMillis(100), () -> {
//			assertTrue(context.getTopicSeq().count() > 5);
//		});
//	}

    private void pauseSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
