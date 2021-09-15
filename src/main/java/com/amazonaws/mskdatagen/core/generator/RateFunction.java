// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core.generator;

import java.util.function.Function;

public class RateFunction {
    private final double rate;
    private final Function<DepsParameters, String> function;

    public RateFunction(double rate, Function<DepsParameters, String> function) {
        this.rate = rate;
        this.function = function;
    }

    public double getRate() {
        return rate;
    }

    public Function<DepsParameters, String> getFunction() {
        return function;
    }

    public Function<DepsParameters, String> getFunctionWithRate() {
        if (rate == 0) return function;
        if (Math.random() >= rate) return function;

        return null;
    }
}
