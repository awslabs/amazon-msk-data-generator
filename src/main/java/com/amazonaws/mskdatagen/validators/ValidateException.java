// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.validators;

public class ValidateException extends IllegalArgumentException {
    private final String prettyErrorMsg;

    public ValidateException(String config, String message) {
        super(message);
        this.prettyErrorMsg = String.format("Configuration problem%n%nProblematic configurations:%n%n - %s", config);
    }

    public String getPrettyErrorMsg() {
        return prettyErrorMsg;
    }
}
