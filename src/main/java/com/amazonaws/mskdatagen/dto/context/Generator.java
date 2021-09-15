// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto.context;

import java.util.List;

public class Generator {
    private final String topic;
    private final String ns;
    private final List<String> attr;
    private final String originalKey;
    private final boolean qualfified;
    private final Strategy strategy;
    private final String expression;
    private final String originalValue;

    public Generator(String topic, String ns, String originalKey, List<String> attr, boolean qualfified, Strategy strategy, String expression, String originalValue) {
        this.topic = topic;
        this.ns = ns;
        this.originalKey = originalKey;
        this.attr = attr;
        this.qualfified = qualfified;
        this.strategy = strategy;
        this.expression = expression;
        this.originalValue = originalValue;
    }

    public String getTopic() {
        return topic;
    }

    public String getNs() {
        return ns;
    }

    public List<String> getAttr() {
        return attr;
    }

    public String getConcatAttr() {
        return attr == null || attr.isEmpty() ? null : String.join("->", attr.toArray(new String[0]));
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public boolean isQualfified() {
        return qualfified;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public String getExpression() {
        return expression;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public String toString() {
        return "Generator{" +
                "topic='" + topic + '\'' +
                ", ns='" + ns + '\'' +
                ", attr=" + attr +
                ", originalKey='" + originalKey + '\'' +
                ", qualfified=" + qualfified +
                ", strategy=" + strategy +
                ", expression='" + expression + '\'' +
                ", originalValue='" + originalValue + '\'' +
                '}';
    }

    public static class GeneratorBuilder {
        private String topic;
        private String ns;
        private String originalKey;
        private List<String> attr;
        private boolean qualfified;
        private Strategy strategy;
        private String expression;
        private String originalValue;

        public GeneratorBuilder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public GeneratorBuilder setNs(String ns) {
            this.ns = ns;
            return this;
        }

        public GeneratorBuilder setOriginalKey(String originalKey) {
            this.originalKey = originalKey;
            return this;
        }

        public GeneratorBuilder setAttr(List<String> attr) {
            this.attr = attr;
            return this;
        }

        public GeneratorBuilder setQualfified(boolean qualfified) {
            this.qualfified = qualfified;
            return this;
        }

        public GeneratorBuilder setStrategy(Strategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public GeneratorBuilder setExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public GeneratorBuilder setOriginalValue(String originalValue) {
            this.originalValue = originalValue;
            return this;
        }

        public Generator build() {
            return new Generator(topic, ns, originalKey, attr, qualfified, strategy, expression, originalValue);
        }
    }

}
