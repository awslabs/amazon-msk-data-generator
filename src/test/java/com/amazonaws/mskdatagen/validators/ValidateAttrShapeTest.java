// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import com.amazonaws.mskdatagen.dto.Namespaces;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Kind;

class ValidateAttrShapeTest {
    @Test
    void validate() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.ATTRIBUTE_PRIMITIVE)
                .setOriginalKey("attrvp.adopters.matching.rate")
                .setTopic("adopters")
                .setNs(Namespaces.getNamespace("attrvp"))
                .setConfig(Arrays.asList("matching", "rate"))
                .setValue("0.05")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.TOPIC)
                .setOriginalKey("topic.adopters.tombstone.rate")
                .setTopic("adopters")
                .setConfig(Arrays.asList("tombstone", "rate"))
                .setValue("0.10")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genk.adopters.name.sometimes.with")
                .setTopic("adopters")
                .setNs(Namespaces.getNamespace("genk"))
                .setAttr("name")
                .setQualified(true)
                .setGenerator("with")
                .setValue("#{Name.full_name}")
                .build());

        Context context = new Context(props);
        ValidateAttrShape validateAttrConfigs = new ValidateAttrShape();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateAttrConfigs.validate(context));

        String expectedMessage = "adopters";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains("attrvp.adopters.matching.rate"));
    }
}
