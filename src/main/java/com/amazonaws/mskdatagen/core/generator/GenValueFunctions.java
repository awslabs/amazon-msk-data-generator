// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core.generator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.Context;
import com.github.javafaker.Faker;

import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.Strategy;
import com.amazonaws.mskdatagen.dto.context.AttrConfigs;
import com.amazonaws.mskdatagen.dto.context.FakerConfig;

public class GenValueFunctions {
    public static final double DEFAULT_NULL_RATE = 0.0;
    public static final double DEFAULT_MATCHING_RATE = 0.1;

    private GenValueFunctions() {
    }

    public static RateFunction getGenValueFunction(Context context, Generator generator) {
        if (generator.getStrategy() == Strategy.ISOLATED) {
            Faker faker = context.getContextMap().get(ConfigType.FAKER_CONFIG).stream().map(t -> ((FakerConfig) t).getFaker())
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find faker object"));

            return isNullable(context, generator, input -> faker.expression(generator.getExpression()));
        } else if (generator.getStrategy() == Strategy.DEPENDENT) {
            List<String> concatAttr = generator.getAttr();
            return isNullable(context, generator, getValueDepsFnByAttr(generator, concatAttr));
        } else if (generator.getStrategy() == Strategy.EITHER) {
            String topic = generator.getTopic();
            List<String> concatAttr = generator.getAttr();
            String expression = generator.getExpression();

            double rate = chooseMatchingRate(context, topic, generator.getNs(), generator.getConcatAttr());

            Function<DepsParameters, String> dependentFunction = getValueDepsFnByAttr(generator, concatAttr);

            return isNullable(context, generator, input -> {
                String dep = dependentFunction.apply(input);

                if (Math.random() <= rate && dep != null) {
                    return dep;
                }
                return context.getContextMap().get(ConfigType.FAKER_CONFIG).stream().map(t -> ((FakerConfig) t).getFaker())
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find faker object"))
                        .expression(expression);
            });
        }

        return null;
    }

    private static Function<DepsParameters, String> getValueDepsFnByAttr(Generator generator, List<String> concatAttr) {
        return input -> {
            DepsParameters.NsKeyValue nsKeyValue = input.getDepTargets().get(generator.getTopic());
            if (nsKeyValue == null) return null;

            Map<List<String>, String> keyOrValueByNs = nsKeyValue.getKeyOrValueByNs(generator.getNs());
            if (concatAttr != null) {
                return keyOrValueByNs.get(concatAttr);
            }
            return keyOrValueByNs.get(Collections.emptyList());
        };
    }

    private static RateFunction isNullable(Context context, Generator generator, Function<DepsParameters, String> function) {
        String topic = generator.getTopic();
        String attr = generator.getConcatAttr();

        double rate = chooseNullRate(context, topic, generator.getNs(), attr);

        return new RateFunction(rate, function);
    }

    private static double chooseNullRate(Context context, String topic, String ns, String attr) {
        List<AttrConfigs> attrConfigs = context.getAttrConfig().collect(Collectors.toList());

        return attrConfigs.stream().filter(t -> t.getTopic().equals(topic))
                .map(AttrConfigs::getNsMap).map(t -> t.getMapByNs(ns))
                .filter(t -> t.containsKey(attr))
                .map(t -> t.get(attr).getNullRate()).findFirst().orElse(DEFAULT_NULL_RATE);
    }

    private static double chooseMatchingRate(Context context, String topic, String ns, String concatAttr) {
        List<AttrConfigs> attrConfigs = context.getAttrConfig().collect(Collectors.toList());

        return attrConfigs.stream().filter(t -> t.getTopic().equals(topic))
                .map(AttrConfigs::getNsMap).map(t -> t.getMapByNs(ns))
                .filter(t -> t.containsKey(concatAttr))
                .map(t -> t.get(concatAttr).getMatchingRate()).findFirst()
                .orElseGet(() -> Optional.ofNullable(context.getGlobalConfig().getMatchingRate())
                        .orElse(DEFAULT_MATCHING_RATE));
    }
}
