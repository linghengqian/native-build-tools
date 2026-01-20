/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.graalvm.buildtools.examples.modulea;

public class Alpha {
    private String message() {
        return "alpha";
    }

    public String revealMessage() throws Exception {
        var method = Alpha.class.getDeclaredMethod("message");
        method.setAccessible(true);
        return (String) method.invoke(this);
    }
}
