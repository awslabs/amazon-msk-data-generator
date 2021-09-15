// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.amazonaws.mskdatagen.dto.context.ConfigType;
import com.amazonaws.mskdatagen.dto.context.GeneratorsConfigs;
import com.amazonaws.mskdatagen.dto.context.TopicSeqConfig;
import com.amazonaws.mskdatagen.dto.context.ContextConfig;

public class TopicSequenceGenerator implements ContextInitializer {
    private final Context context;

    public TopicSequenceGenerator(Context context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        List<String> collect = context.getGenerators()
                .map(GeneratorsConfigs::getTopic).collect(Collectors.toList());

        List<ContextConfig> topicsSeq = new CopyOnWriteArrayList<>();
        context.getContextMap().put(ConfigType.TOPIC_SEQ_CONFIG, topicsSeq);

        CompletableFuture.runAsync(() -> {
            int i = 1;
            while (i > 0) {
                int k = i / collect.size();

                TopicSeqConfig topicSeqConfig = new TopicSeqConfig(collect.get(i - (k * collect.size())));
                topicsSeq.add(topicSeqConfig);
                i++;
                if (i == Integer.MIN_VALUE) {
                    i = 1;
                }
            }
        });
    }
}
