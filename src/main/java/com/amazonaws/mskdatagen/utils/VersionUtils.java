// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.utils;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtils {
    private static final Logger log = LoggerFactory.getLogger(VersionUtils.class);

    private VersionUtils() {
    }

    public static String pomVersion() {
        try {
            Properties pomProps = new Properties();
            InputStream resourceAsStream = VersionUtils.class.getClassLoader()
                    .getResourceAsStream("META-INF/maven/software.aws.msk/msk-data-generator/pom.properties");
            pomProps.load(resourceAsStream);
            return pomProps.getProperty("version");
        } catch (Exception e) {
            return "v0.0";
        }
    }
}
