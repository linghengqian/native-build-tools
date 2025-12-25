/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.graalvm.buildtools.examples.moduleb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BetaTest {

    @Test
    void usesRecordReflectively() throws Exception {
        Beta beta = new Beta("beta");
        assertEquals("BETA", beta.reflectUppercase());
    }
}
