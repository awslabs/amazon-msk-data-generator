// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen;

import java.util.List;
import java.util.Map;

import com.amazonaws.mskdatagen.core.Context;
import com.amazonaws.mskdatagen.core.ContextCreator;
import com.amazonaws.mskdatagen.producer.GenerateSourceRecord;
import com.amazonaws.mskdatagen.utils.VersionUtils;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorConnectorTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(GeneratorConnectorTask.class);

    private GenerateSourceRecord generateSourceRecord;

    @Override
    public void start(Map<String, String> props) {
        if (props.isEmpty()) {
            String message = "No usable properties - refusing to start since there is no work to do.";
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        log.info("Creating a context.");
        log.debug("Creating a context with properties = {}", props);

        Context context = new ContextCreator().makeContext(props);
        this.generateSourceRecord = new GenerateSourceRecord(context);
    }

    @Override
    public void stop() {
    }

    @Override
    public List<SourceRecord> poll() {
        log.info("Generate source record");

        return generateSourceRecord.generateSourceRecord();
    }

    @Override
    public String version() {
        return VersionUtils.pomVersion();
    }
}
