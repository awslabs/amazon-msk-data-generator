// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class GlobalConfigs implements ContextConfig {
    private Long historyRecordsMax;
    private Double matchingRate;
    private Long throttleMs;

    public Long getHistoryRecordsMax() {
        return historyRecordsMax;
    }

    public Double getMatchingRate() {
        return matchingRate;
    }

    public Long getThrottleMs() {
        return throttleMs;
    }

    public void setHistoryRecordsMax(Long historyRecordsMax) {
        this.historyRecordsMax = historyRecordsMax;
    }

    public void setMatchingRate(Double matchingRate) {
        this.matchingRate = matchingRate;
    }

    public void setThrottleMs(Long throttleMs) {
        this.throttleMs = throttleMs;
    }

    @Override
    public String getTopic() {
        return "";
    }

    @Override
    public String toString() {
        return "GlobalConfigs{" +
                "historyRecordsMax=" + historyRecordsMax +
                ", matchingRate=" + matchingRate +
                ", throttleMs=" + throttleMs +
                '}';
    }
}
