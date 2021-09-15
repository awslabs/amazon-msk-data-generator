// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.Namespaces;
import com.amazonaws.mskdatagen.dto.ConfigBuilder;
import org.junit.jupiter.api.Test;

import com.amazonaws.mskdatagen.dto.Kind;

class ValidateDependenciesTest {
    @Test
    void validateTopic() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genv.cats.owner.matching")
                .setTopic("cats")
                .setNs(Namespaces.getNamespace("genv"))
                .setAttr("owner")
                .setGenerator("matching")
                .setValue("owners.key")
                .build());

        Context context = new Context(props);
        ValidateDependencies validateAttrConfigs = new ValidateDependencies();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateAttrConfigs.validate(context));

        String expectedMessage = "Found a generator for topic cats that is dependent on topic owners";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains("genv.cats.owner.matching"));
    }

    @Test
    void validateAttr() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genv.cats.owner.matching")
                .setTopic("cats")
                .setNs(Namespaces.getNamespace("genv"))
                .setAttr("owner")
                .setGenerator("matching")
                .setValue("owners.key")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genv.owners.creditCardNumber.with")
                .setTopic("owners")
                .setNs(Namespaces.getNamespace("genv"))
                .setAttr("creditCardNumber")
                .setGenerator("with")
                .setValue("#{Internet.uuid}")
                .build());

        Context context = new Context(props);
        ValidateDependencies validateAttrConfigs = new ValidateDependencies();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateAttrConfigs.validate(context));

        String expectedMessage = "Found a generator for topic cats that is dependent on topic owners's key";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains("genv.cats.owner.matching"));
    }
}
