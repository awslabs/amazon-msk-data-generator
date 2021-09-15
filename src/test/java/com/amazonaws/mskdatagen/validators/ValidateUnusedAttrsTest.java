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
import com.amazonaws.mskdatagen.dto.Kind;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Namespaces;

class ValidateUnusedAttrsTest {
    @Test
    void validateWithValidateException() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.ATTRIBUTE_COMPLEX)
                .setOriginalKey("attrk.diets.name.matching.rate")
                .setTopic("diets")
                .setNs(Namespaces.getNamespace("attrk"))
                .setConfig(Arrays.asList("matching", "rate"))
                .setValue("0.05")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.TOPIC)
                .setOriginalKey("topic.diets.tombstone.rate")
                .setTopic("diets")
                .setConfig(Arrays.asList("tombstone", "rate"))
                .setValue("0.10")
                .build());

        Context context = new Context(props);
        ValidateUnusedAttrs validateUnusedAttrs = new ValidateUnusedAttrs();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateUnusedAttrs.validate(context));

        String expectedMessage = "diets";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains("attrk.diets.name.matching.rate"));
    }

    @Test
    void validateSuccess() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.ATTRIBUTE_COMPLEX)
                .setOriginalKey("attrk.diets.name.matching.rate")
                .setTopic("diets")
                .setAttr("name")
                .setNs(Namespaces.getNamespace("attrk"))
                .setConfig(Arrays.asList("matching", "rate"))
                .setValue("0.05")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.TOPIC)
                .setOriginalKey("topic.diets.tombstone.rate")
                .setTopic("diets")
                .setConfig(Arrays.asList("tombstone", "rate"))
                .setValue("0.10")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genk.diets.name.sometimes.with")
                .setTopic("diets")
                .setNs(Namespaces.getNamespace("genk"))
                .setAttr("name")
                .setQualified(true)
                .setGenerator("with")
                .setValue("#{Name.full_name}")
                .build());

        Context context = new Context(props);
        ValidateUnusedAttrs validateUnusedAttrs = new ValidateUnusedAttrs();
        validateUnusedAttrs.validate(context);
    }
}
