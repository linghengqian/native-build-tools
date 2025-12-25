/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.graalvm.buildtools.examples.modulea;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlphaTest {

    @Test
    void usesReflection() throws Exception {
        Alpha alpha = new Alpha();
        assertEquals("alpha", alpha.revealMessage());
    }
}
