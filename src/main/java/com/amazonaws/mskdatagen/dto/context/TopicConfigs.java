// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

public class TopicConfigs implements ContextConfig {
    private final String topic;
    private long historyRecordsMax;
    private long throttleMs;
    private double tombstoneRate;
    private long recordsExactly;

    public TopicConfigs(String topic) {
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public long getHistoryRecordsMax() {
        return historyRecordsMax;
    }

    public void setHistoryRecordsMax(long historyRecordsMax) {
        this.historyRecordsMax = historyRecordsMax;
    }

    public long getThrottleMs() {
        return throttleMs;
    }

    public void setThrottleMs(long throttleMs) {
        this.throttleMs = throttleMs;
    }

    public double getTombstoneRate() {
        return tombstoneRate;
    }

    public void setTombstoneRate(double tombstoneRate) {
        this.tombstoneRate = tombstoneRate;
    }

    public long getRecordsExactly() {
        return recordsExactly;
    }

    public void setRecordsExactly(long recordsExactly) {
        if (recordsExactly <= 0) {
            throw new IllegalArgumentException("records.exactly must be greater than 0, value: " + recordsExactly);
        }
        this.recordsExactly = recordsExactly;
    }

    @Override
    public String toString() {
        return "TopicConfigs{" +
                "topic='" + topic + '\'' +
                ", historyRecordsMax=" + historyRecordsMax +
                ", throttleMs=" + throttleMs +
                ", tombstoneRate=" + tombstoneRate +
                ", recordsExactly=" + recordsExactly +
                '}';
    }
}
