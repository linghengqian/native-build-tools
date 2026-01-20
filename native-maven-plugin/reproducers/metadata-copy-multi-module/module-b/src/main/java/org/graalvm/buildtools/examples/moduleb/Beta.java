/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.graalvm.buildtools.examples.moduleb;

public record Beta(String value) {
    public String reflectUppercase() throws Exception {
        var constructor = Beta.class.getDeclaredConstructor(String.class);
        return constructor.newInstance(value).value().toUpperCase();
    }
}
