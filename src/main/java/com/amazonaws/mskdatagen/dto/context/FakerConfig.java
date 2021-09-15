// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import com.github.javafaker.Faker;

public class FakerConfig implements ContextConfig {
    private final Faker faker;

    public FakerConfig(Faker faker) {
        this.faker = faker;
    }

    public Faker getFaker() {
        return faker;
    }

    @Override
    public String getTopic() {
        return "";
    }
}
