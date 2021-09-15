// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.core.generator.DepsParameters;
import com.amazonaws.mskdatagen.core.generator.ResultGen;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.Generator;
import com.amazonaws.mskdatagen.dto.context.Strategy;
import com.amazonaws.mskdatagen.dto.context.TopicConfigs;
import com.amazonaws.mskdatagen.core.generator.GenValueFunctions;
import com.amazonaws.mskdatagen.core.generator.RateFunction;
import com.amazonaws.mskdatagen.core.generator.VerifyDepsFunctions;

public class CompileGeneratorStrategies implements ContextInitializer {
    private final Context context;

    public CompileGeneratorStrategies(Context context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        List<GeneratorsConfigs> generatorsConfigs = context.getGenerators().collect(Collectors.toList());

        for (GeneratorsConfigs generatorsConfig : generatorsConfigs) {
            genNamespace(context, generatorsConfig, generatorsConfig.getKeys(), "key");
            genNamespace(context, generatorsConfig, generatorsConfig.getValue(), "value");
        }
    }

    private void genNamespace(Context context, GeneratorsConfigs generatorsConfig, Map<List<String>, List<Generator>> namespaces, String ns) {
        if (namespaces.containsKey(Collections.emptyList())) {
            List<Generator> generators = namespaces.get(Collections.emptyList());

            Generator generator = compileGeneratorStrategy(generators);
            Predicate<DepsParameters> verifyDepsFunction = VerifyDepsFunctions.getVerifyDepsFunction(Objects.requireNonNull(generator));
            RateFunction genValueFunction = GenValueFunctions.getGenValueFunction(context, generator);
            Function<DepsParameters, ResultGen> depsResultGenFunction = compileSoloGen(verifyDepsFunction, genValueFunction);

            Function<DepsParameters, ResultGen> maybeTombstone = maybeTombstone(context, generatorsConfig.getTopic(), ns, depsResultGenFunction);
            setFuncInGenConfig(generatorsConfig, ns, maybeTombstone);
        } else if (!namespaces.keySet().isEmpty()) {
            Map<List<String>, AttrFunctionPair> attrFns = new HashMap<>();
            for (Map.Entry<List<String>, List<Generator>> attrsList : namespaces.entrySet()) {
                Generator generator = compileGeneratorStrategy(attrsList.getValue());
                Predicate<DepsParameters> verifyDepsFunction = VerifyDepsFunctions.getVerifyDepsFunction(Objects.requireNonNull(generator));
                RateFunction genValueFunction = GenValueFunctions.getGenValueFunction(context, generator);

                AttrFunctionPair attrFunctionPair = new AttrFunctionPair(verifyDepsFunction, genValueFunction);
                attrFns.put(attrsList.getKey(), attrFunctionPair);
            }

            Function<DepsParameters, ResultGen> depsResultGenFunction = compileAttrsGen(attrFns);
            Function<DepsParameters, ResultGen> maybeTombstone = maybeTombstone(context, generatorsConfig.getTopic(), ns, depsResultGenFunction);
            setFuncInGenConfig(generatorsConfig, ns, maybeTombstone);
        }
    }

    private void setFuncInGenConfig(GeneratorsConfigs generatorsConfig, String ns, Function<DepsParameters, ResultGen> maybeTombstone) {
        if ("key".equals(ns)) {
            generatorsConfig.setKeyF(maybeTombstone);
        } else {
            generatorsConfig.setValueF(maybeTombstone);
        }
    }

    private Function<DepsParameters, ResultGen> compileAttrsGen(Map<List<String>, AttrFunctionPair> attrFns) {
        return input -> {
            Map<List<String>, String> result = new HashMap<>();
            for (Map.Entry<List<String>, AttrFunctionPair> listAttrFunctionPairEntry : attrFns.entrySet()) {
                AttrFunctionPair value = listAttrFunctionPairEntry.getValue();
                if (value.getVerifyDepsFunction().test(input)) {
                    String genValue = value.getGenValueFunction().getFunctionWithRate().apply(input);
                    result.put(listAttrFunctionPairEntry.getKey(), genValue);
                } else {
                    return new ResultGen(false);
                }
            }
            return new ResultGen(true, result);
        };
    }

    private Function<DepsParameters, ResultGen> maybeTombstone(Context context, String topic, String ns, Function<DepsParameters, ResultGen> depsResultGenFunction) {
        Double topicRate = context.getTopicConfigs()
                .filter(t -> topic.equals(t.getTopic()))
                .findFirst().map(TopicConfigs::getTombstoneRate).orElse(null);

        if ("value".equals(ns) && topicRate != null) {
            return input -> {
                if (Math.random() >= topicRate) {
                    return depsResultGenFunction.apply(input);
                }
                return new ResultGen(false);
            };
        }
        return depsResultGenFunction;
    }

    private Function<DepsParameters, ResultGen> compileSoloGen(Predicate<DepsParameters> verifyDepsFunction, RateFunction genValueFunction) {
        return input -> {
            if (verifyDepsFunction.test(input)) {
                String genValue = genValueFunction.getFunctionWithRate().apply(input);
                return new ResultGen(true, Collections.singletonMap(Collections.emptyList(), genValue));
            }
            return new ResultGen(false);
        };
    }

    private Generator compileGeneratorStrategy(List<Generator> generators) {
        List<Generator> sortedGenerators = sortStrategies(generators);

        if (isIsolatedAndNotQualified(sortedGenerators)
                || isDependentAndNotQualified(sortedGenerators)) {
            return sortedGenerators.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find generator for IsolatedAndNotQualified"));
        } else if (isQualifiedIsolatedDependent(sortedGenerators)) {
            String expression = sortedGenerators.stream().skip(1)
                    .findAny().map(Generator::getExpression).orElseThrow(() -> new IllegalArgumentException("Can't find generator for isQualifiedIsolatedDependent"));

            Optional<Generator> firstGenerator = sortedGenerators.stream().findFirst();

            if (firstGenerator.isPresent()) {
                Generator generator = firstGenerator.get();
                return new Generator.GeneratorBuilder()
                        .setStrategy(Strategy.EITHER)
                        .setExpression(expression)
                        .setTopic(generator.getTopic())
                        .setNs(generator.getNs())
                        .setAttr(generator.getAttr())
                        .build();
            }
        }

        throw new IllegalArgumentException("Any generators were not find, input list: " + generators);
    }

    private boolean isQualifiedIsolatedDependent(List<Generator> sortedGenerators) {
        long isolatedAndQualfified = sortedGenerators.stream()
                .filter(t -> t.getStrategy() == Strategy.ISOLATED && t.isQualfified())
                .count();

        long dependentAndQualfified = sortedGenerators.stream()
                .filter(t -> t.getStrategy() == Strategy.DEPENDENT && t.isQualfified())
                .count();

        return isolatedAndQualfified > 0 && isolatedAndQualfified == dependentAndQualfified;
    }

    private boolean isIsolatedAndNotQualified(List<Generator> generators) {
        return generators.stream()
                .filter(t -> t.getStrategy() == Strategy.ISOLATED && !t.isQualfified())
                .count() == generators.size();
    }

    private boolean isDependentAndNotQualified(List<Generator> generators) {
        return generators.stream()
                .filter(t -> t.getStrategy() == Strategy.DEPENDENT && !t.isQualfified())
                .count() == generators.size();
    }

    private List<Generator> sortStrategies(List<Generator> generators) {
        return generators.stream()
                .sorted(Comparator.comparing((Generator generator) -> generator.getStrategy().toString())
                        .thenComparing(Generator::isQualfified))
                .collect(Collectors.toList());
    }

    private static class AttrFunctionPair {
        private final Predicate<DepsParameters> verifyDepsFunction;
        private final RateFunction genValueFunction;

        public AttrFunctionPair(Predicate<DepsParameters> verifyDepsFunction, RateFunction genValueFunction) {
            this.verifyDepsFunction = verifyDepsFunction;
            this.genValueFunction = genValueFunction;
        }

        public Predicate<DepsParameters> getVerifyDepsFunction() {
            return verifyDepsFunction;
        }

        public RateFunction getGenValueFunction() {
            return genValueFunction;
        }
    }
}
