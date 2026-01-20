/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */
package org.graalvm.buildtools.maven

class MultiModuleMetadataCopyFunctionalTest extends AbstractGraalVMMavenFunctionalTest {

    private static final List<String> LEGACY_FILES = ['jni-config.json', 'proxy-config.json', 'reflect-config.json', 'resource-config.json', 'serialization-config.json']

    private boolean metadataExistsAt(String path) {
        def base = file(path)
        if (!base.exists()) {
            return false
        }
        if (new File(base, "reachability-metadata.json").exists()) {
            return true
        }
        return LEGACY_FILES.every { new File(base, it).exists() }
    }

    def "metadata copy works for each module in multi-module project"() {
        given:
        withReproducer("metadata-copy-multi-module")

        when:
        mvn '-Pmetadata-copy', '-DskipNativeTests', 'test', 'native:metadata-copy'

        then:
        buildSucceeded
        metadataExistsAt("module-a/target/native/metadata")
        metadataExistsAt("module-b/target/native/metadata")
    }
}
