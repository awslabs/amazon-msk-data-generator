// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.mskdatagen.utils.VersionUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

public class GeneratorSourceConnector extends SourceConnector {

    private Map<String, String> props;

    @Override
    public String version() {
        // For some reason, the JVM blows up if I try to call out
        // to Clojure here.
        return VersionUtils.pomVersion();
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GeneratorConnectorTask.class;
    }

    @Override
    public void start(Map<String, String> props) {
        this.props = props;
    }

    @Override
    public void stop() {
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();

        for (int k = 0; k < maxTasks; k++) {
            Map<String, String> taskConfig = new HashMap<>(this.props);
            taskConfigs.add(taskConfig);
        }

        return taskConfigs;
    }

    @Override
    public ConfigDef config() {
        return GeneratorConfig.conf();
    }
}
