// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: EPL-2.0

package com.amazonaws.mskdatagen.dto;

import java.util.HashMap;
import java.util.Map;

public enum Namespaces {
    GENKP("genkp", "key"),
    GENK("genk", "key"),
    GENVP("genvp", "value"),
    GENV("genv", "value"),
    ATTRKP("attrkp", "key"),
    ATTRK("attrk", "key"),
    ATTRVP("attrvp", "value"),
    ATTRV("attrv", "value");

    private static final Map<String, String> namespaces = new HashMap<>();

    static {
        for (Namespaces e : values()) {
            namespaces.put(e.type, e.ns);
        }
    }

    private final String type;
    private final String ns;

    Namespaces(String type, String ns) {
        this.type = type;
        this.ns = ns;
    }

    public static String getNamespace(String type) {
        return namespaces.get(type);
    }
}
