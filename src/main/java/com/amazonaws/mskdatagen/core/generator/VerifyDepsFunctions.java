// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core.generator;

import java.util.Objects;
import java.util.function.Predicate;

import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.Strategy;

public class VerifyDepsFunctions {
    private VerifyDepsFunctions() {
    }

    public static Predicate<DepsParameters> getVerifyDepsFunction(Generator generator) {
        if (generator.getStrategy() == Strategy.DEPENDENT) {
            return dependentFunction;
        }

        return input -> true;
    }

    private static final Predicate<DepsParameters> dependentFunction = input -> {
        if (input.getDeps() != null) {
            return input.getDepTargets().values().stream().noneMatch(Objects::isNull);
        }
        return false;
    };
}
