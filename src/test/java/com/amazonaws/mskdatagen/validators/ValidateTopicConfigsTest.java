// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Test;

class ValidateTopicConfigsTest {
    public static final String WRONG_TOPIC = "topic.diets2.tombstone.rat";

    @Test
    void validate() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder()
                .setKind(Kind.ATTRIBUTE_COMPLEX)
                .setTopic("cats")
                .setOriginalKey("genk.cats.name.with")
                .setGenerator("cats")
                .setValue("test")
                .build());

        props.add(new ConfigBuilder()
                .setKind(Kind.ATTRIBUTE_COMPLEX)
                .setTopic("diets")
                .setGenerator("cats")
                .setOriginalKey("genk.diets.catName.matching")
                .setValue("test")
                .build());

        props.add(new ConfigBuilder()
                .setKind(Kind.TOPIC)
                .setTopic("diets2")
                .setOriginalKey(WRONG_TOPIC)
                .setValue("test")
                .build());

        Context context = new Context(props);
        ValidateTopicConfigs validateTopicConfigs = new ValidateTopicConfigs();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateTopicConfigs.validate(context));

        String expectedMessage = "diets";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains(WRONG_TOPIC));
    }
}
