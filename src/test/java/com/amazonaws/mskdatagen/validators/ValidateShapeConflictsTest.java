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

import com.amazonaws.mskdatagen.dto.Namespaces;

class ValidateShapeConflictsTest {
    @Test
    void validate() {
        List<Config> props = new ArrayList<>();

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_PRIMITIVE)
                .setOriginalKey("genkp.owners.with")
                .setTopic("owners")
                .setNs(Namespaces.getNamespace("genkp"))
                .setGenerator("with")
                .setValue("#{Name.full_name}")
                .build());

        props.add(new ConfigBuilder().setKind(Kind.GENERATE_COMPLEX)
                .setOriginalKey("genk.owners.test.with")
                .setTopic("owners")
                .setNs(Namespaces.getNamespace("genk"))
                .setAttr("test")
                .setGenerator("with")
                .setValue("2")
                .build());

        Context context = new Context(props);
        ValidateShapeConflicts validateAttrConfigs = new ValidateShapeConflicts();

        ValidateException exception = assertThrows(ValidateException.class, () -> validateAttrConfigs.validate(context));

        String expectedMessage = "owners";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue(exception.getPrettyErrorMsg().contains("genk.owners.test.with"));
        assertTrue(exception.getPrettyErrorMsg().contains("genkp.owners.with"));
    }
}
