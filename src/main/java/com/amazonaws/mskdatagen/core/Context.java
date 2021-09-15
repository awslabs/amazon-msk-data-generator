// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.amazonaws.mskdatagen.dto.Config;
import com.amazonaws.mskdatagen.dto.context.GeneratedConfig;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.github.javafaker.Faker;

import com.amazonaws.mskdatagen.dto.Kind;
import com.amazonaws.mskdatagen.dto.context.AttrConfig;
import com.amazonaws.mskdatagen.dto.context.AttrConfigs;
import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;
import com.amazonaws.mskdatagen.dto.context.FakerConfig;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.GlobalConfigs;
import com.amazonaws.mskdatagen.dto.context.HistoryConfigs;
import com.amazonaws.mskdatagen.dto.context.RecordsProducedConfig;
import com.amazonaws.mskdatagen.dto.context.RetainedConfig;
import com.amazonaws.mskdatagen.dto.context.Strategy;
import com.amazonaws.mskdatagen.dto.context.TimestampsConfig;
import com.amazonaws.mskdatagen.dto.context.TopicConfigs;
import com.amazonaws.mskdatagen.dto.context.TopicSeqConfig;

public class Context {
    private final Map<ConfigType, List<ContextConfig>> contextMap = new ConcurrentHashMap<>();

    public Context(List<Config> configs) {
        contextMap.put(ConfigType.FAKER_CONFIG, Collections.singletonList(new FakerConfig(new Faker())));
        addGlobalConfigs(configs);
        addTopicConfigs(configs);
        addAttrConfigs(configs);
        addGenerators(configs);
    }

    public Map<ConfigType, List<ContextConfig>> getContextMap() {
        return contextMap;
    }

    public Stream<RetainedConfig> getRetainedConfig() {
        return safeRead(ConfigType.RETAINED_CONFIG, RetainedConfig.class);
    }

    public Stream<GeneratorsConfigs> getGenerators() {
        return safeRead(ConfigType.GENERATORS_CONFIG, GeneratorsConfigs.class);
    }

    public Stream<AttrConfigs> getAttrConfig() {
        return safeRead(ConfigType.ATTR_CONFIG, AttrConfigs.class);
    }

    public GeneratedConfig getGenerated() {
        return safeRead(ConfigType.GENERATED_CONFIG, GeneratedConfig.class)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find Generated Config"));
    }

    public Stream<HistoryConfigs> getHistories() {
        return safeRead(ConfigType.HISTORY_CONFIG, HistoryConfigs.class);
    }

    public Stream<TopicConfigs> getTopicConfigs() {
        return safeRead(ConfigType.TOPIC_CONFIG, TopicConfigs.class);
    }

    public Stream<TopicSeqConfig> getTopicSeq() {
        return safeRead(ConfigType.TOPIC_SEQ_CONFIG, TopicSeqConfig.class);
    }

    public TimestampsConfig getTimestampsConfig() {
        return safeRead(ConfigType.TIMESTAMPS_CONFIG, TimestampsConfig.class)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find Timestamp Config"));
    }

    public RecordsProducedConfig getRecordsProducedConfig() {
        return safeRead(ConfigType.RECORDS_PRODUCED_CONFIG, RecordsProducedConfig.class)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find RecordsProduced Config"));
    }

    public GlobalConfigs getGlobalConfig() {
        return safeRead(ConfigType.GLOBAL_CONFIG, GlobalConfigs.class)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find Global Config"));
    }

    private <T> Stream<T> safeRead(ConfigType configType, Class<T> clazz) {
        return Optional.ofNullable(contextMap.get(configType))
                .map(hConfig -> hConfig.stream().map(clazz::cast))
                .orElse(Stream.empty());
    }

    private void addAttrConfigs(List<Config> configs) {
        RetainedConfig retainedConfig
                = (RetainedConfig) getConfig(ConfigType.RETAINED_CONFIG, ConfigType.ATTR_CONFIG.getConfigGroup());

        configs.forEach(config -> {
            if (config.getKind() == Kind.ATTRIBUTE_COMPLEX || config.getKind() == Kind.ATTRIBUTE_PRIMITIVE) {
                AttrConfigs attrConfigs = (AttrConfigs) getConfig(ConfigType.ATTR_CONFIG, config.getTopic());

                if ("key".equals(config.getNs())) {
                    Map<String, AttrConfig> key = attrConfigs.getNsMap().getKey();
                    AttrConfig attrConfig = key.computeIfAbsent(config.getAttr(), t -> new AttrConfig());

                    addAttrConfig(config, attrConfig);
                } else if ("value".equals(config.getNs())) {
                    Map<String, AttrConfig> value = attrConfigs.getNsMap().getValue();
                    AttrConfig attrConfig = value.computeIfAbsent(config.getAttr(), t -> new AttrConfig());

                    addAttrConfig(config, attrConfig);
                }
                retainedConfig.addOriginalConfigs(config);
            }
        });
    }

    private void addGlobalConfigs(List<Config> configs) {
        GlobalConfigs globalConfigs = (GlobalConfigs) getConfig(ConfigType.GLOBAL_CONFIG, "global");
        configs.forEach(config -> {
            if (config.getKind() == Kind.GLOBAL) {
                Optional.ofNullable(config.getConfigs()).ifPresent(t -> {
                    String join = String.join(".", t);
                    switch (join) {
                        case "throttle.ms":
                            globalConfigs.setThrottleMs(Long.parseLong(config.getValue()));
                            break;
                        case "history.records.max":
                            globalConfigs.setHistoryRecordsMax(Long.parseLong(config.getValue()));
                            break;
                        case "matching.rate":
                            globalConfigs.setMatchingRate(Double.parseDouble(config.getValue()));
                            break;
                        default:
                            String message = String.format("Unrecognized global configuration: %s", config.getOriginalKey());
                            throw new IllegalArgumentException(message);
                    }
                });
            }
        });
    }

    private void addTopicConfigs(List<Config> configs) {
        RetainedConfig retainedConfig
                = (RetainedConfig) getConfig(ConfigType.RETAINED_CONFIG, ConfigType.TOPIC_CONFIG.getConfigGroup());

        configs.forEach(config -> {
            if (config.getKind() == Kind.TOPIC) {
                TopicConfigs topicConfigs
                        = (TopicConfigs) getConfig(ConfigType.TOPIC_CONFIG, config.getTopic());

                Optional.ofNullable(config.getConfigs()).ifPresent(t -> {
                    String join = String.join(".", t);
                    switch (join) {
                        case "throttle.ms":
                            topicConfigs.setThrottleMs(Long.parseLong(config.getValue()));
                            break;
                        case "history.records.max":
                            topicConfigs.setHistoryRecordsMax(Long.parseLong(config.getValue()));
                            break;
                        case "tombstone.rate":
                            topicConfigs.setTombstoneRate(Double.parseDouble(config.getValue()));
                            break;
                        case "records.exactly":
                            topicConfigs.setRecordsExactly(Long.parseLong(config.getValue()));
                            break;
                        default:
                            String message = String.format("Unrecognized topic configuration: %s", config.getOriginalKey());
                            throw new IllegalArgumentException(message);
                    }
                });
                retainedConfig.addOriginalConfigs(config);
            }
        });
    }

    private void addGenerators(List<Config> configs) {
        RetainedConfig retainedConfig
                = (RetainedConfig) getConfig(ConfigType.RETAINED_CONFIG, ConfigType.GENERATORS_CONFIG.getConfigGroup());

        configs.forEach(config -> {
            if (config.getGenerator() != null) {
                GeneratorsConfigs attrConfigs = (GeneratorsConfigs) getConfig(ConfigType.GENERATORS_CONFIG, config.getTopic());

                Generator.GeneratorBuilder generatorBuilder = new Generator.GeneratorBuilder();
                fillGeneratorByStrategy(generatorBuilder, config);
                generatorBuilder.setOriginalKey(config.getOriginalKey());
                generatorBuilder.setQualfified(config.isQualified());
                generatorBuilder.setOriginalValue(config.getValue());
                Generator generator = generatorBuilder.build();

                List<String> attrKey = config.getSplitAttr();
                if ("key".equals(config.getNs())) {
                    attrConfigs.getKeys()
                            .computeIfAbsent(attrKey, k -> new ArrayList<>()).add(generator);
                } else if ("value".equals(config.getNs())) {
                    attrConfigs.getValue()
                            .computeIfAbsent(attrKey, k -> new ArrayList<>()).add(generator);
                }

                if (generator.getStrategy() == Strategy.DEPENDENT) {
                    attrConfigs.getDependencies().add(generator.getTopic());
                }

                retainedConfig.addOriginalConfigs(config);
            }
        });
    }

    private void fillAttr(Config config, Generator.GeneratorBuilder generatorBuilder) {
        if (config.getAttr() == null) {
            generatorBuilder.setAttr(null);
        } else {
            generatorBuilder.setAttr(config.getSplitAttr());
        }
    }

    private void fillGeneratorByStrategy(Generator.GeneratorBuilder generatorBuilder, Config config) {
        if ("with".equals(config.getGenerator())) {
            generatorBuilder.setStrategy(Strategy.ISOLATED);
            generatorBuilder.setTopic(config.getTopic());
            generatorBuilder.setNs(config.getNs());
            generatorBuilder.setExpression(config.getValue());
            fillAttr(config, generatorBuilder);
        } else if ("matching".equals(config.getGenerator())) {
            generatorBuilder.setStrategy(Strategy.DEPENDENT);
            String[] values = config.getValue().split("\\.");
            String topic = values[0];
            String ns = values[1];
            generatorBuilder.setTopic(topic);
            generatorBuilder.setNs(ns);
            if (values.length > 2) {
                String[] attrs = values[2].split("->");
                generatorBuilder.setAttr(Arrays.asList(attrs));
            }
        }
    }

    private ContextConfig getConfig(ConfigType configType, String topic) {
        List<ContextConfig> contextConfigs = contextMap.computeIfAbsent(configType, k -> new ArrayList<>());
        return contextConfigs.stream().filter(t -> topic.equals(t.getTopic())).findFirst()
                .orElseGet(() -> createNewConfig(configType, topic, contextConfigs));
    }

    private ContextConfig createNewConfig(ConfigType configType, String topic, List<ContextConfig> contextConfigs) {
        ContextConfig newConfig = null;
        if (configType == ConfigType.GLOBAL_CONFIG) {
            newConfig = new GlobalConfigs();
        } else if (configType == ConfigType.RETAINED_CONFIG) {
            newConfig = new RetainedConfig(topic);
        } else if (configType == ConfigType.TOPIC_CONFIG) {
            newConfig = new TopicConfigs(topic);
        } else if (configType == ConfigType.ATTR_CONFIG) {
            newConfig = new AttrConfigs(topic);
        } else if (configType == ConfigType.GENERATORS_CONFIG) {
            newConfig = new GeneratorsConfigs(topic);
        }

        contextConfigs.add(newConfig);
        return newConfig;
    }

    private void addAttrConfig(Config config, AttrConfig attrConfig) {
        Optional.ofNullable(config.getConfigs()).ifPresent(t -> {

            String join = String.join(".", t);
            switch (join) {
                case "null.rate":
                    attrConfig.setNullRate(Double.parseDouble(config.getValue()));
                    break;
                case "matching.rate":
                    attrConfig.setMatchingRate(Double.parseDouble(config.getValue()));
                    break;
                default:
                    String message = String.format("Unrecognized topic configuration: %s", config.getOriginalKey());
                    throw new IllegalArgumentException(message);
            }
        });
    }
}
