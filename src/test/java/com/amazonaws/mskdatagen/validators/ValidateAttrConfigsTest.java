// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Test;

class ValidateAttrConfigsTest {
    public static final String WRONG_ATTR = "attrk.cats.name.matching.rate";

    @Test
    void validate() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder()
                .setKind(Kind.ATTRIBUTE_COMPLEX)
                .setTopic("cats")
                .setAttr("name")
                .setGenerator("cats")
                .setOriginalKey(WRONG_ATTR)
                .setValue("test")
                .build());

        props.add(new ConfigBuilder()
                .setKind(Kind.TOPIC)
                .setOriginalKey("topic.diets.tombstone.rate")
                .setTopic("diets")
                .setValue("test")
                .build());

        Context context = new Context(props);
        ValidateAttrConfigs validateAttrConfigs = new ValidateAttrConfigs();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateAttrConfigs.validate(context));

        String expectedMessage = "cats";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains(WRONG_ATTR));
    }
}
