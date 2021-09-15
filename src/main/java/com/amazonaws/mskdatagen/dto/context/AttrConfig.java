// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class AttrConfig {
    private double nullRate;
    private double matchingRate;

    public double getNullRate() {
        return nullRate;
    }

    public void setNullRate(double nullRate) {
        this.nullRate = nullRate;
    }

    public double getMatchingRate() {
        return matchingRate;
    }

    public void setMatchingRate(double matchingRate) {
        this.matchingRate = matchingRate;
    }

    @Override
    public String toString() {
        return "AttrConfig{" +
                "nullRate=" + nullRate +
                ", matchingRate=" + matchingRate +
                '}';
    }
}
