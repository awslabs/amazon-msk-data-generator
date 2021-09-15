// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.parsers;

import com.amazonaws.mskdatagen.dto.Config;

public interface Parser {
    Config parse(String[] tokens, String key, String value);
}
