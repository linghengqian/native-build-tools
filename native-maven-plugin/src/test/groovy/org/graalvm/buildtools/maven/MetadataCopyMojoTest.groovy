/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.buildtools.maven

import org.apache.maven.model.Build
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.logging.Logger
import org.codehaus.plexus.logging.console.ConsoleLogger
import org.graalvm.buildtools.maven.config.agent.AgentConfiguration
import org.graalvm.buildtools.maven.config.agent.MetadataCopyConfiguration
import spock.lang.Specification
import spock.lang.TempDir

import java.util.Collections

class MetadataCopyMojoTest extends Specification {

    @TempDir
    File tempDir

    def "reuses existing metadata when output directory is shared across modules"() {
        given:
        File buildDir = new File(tempDir, "module/target")
        File mainOutput = new File(buildDir, "native/agent-output/main")
        File testOutput = new File(buildDir, "native/agent-output/test")
        testOutput.mkdirs()
        new File(testOutput, "reachability-metadata.json").text = "{}"

        File destinationDir = new File(tempDir, "shared-output")
        destinationDir.mkdirs()
        new File(destinationDir, "reachability-metadata.json").text = "{}"

        File logFile = new File(tempDir, "native-image-configure-args.txt")
        File executable = new File(tempDir, "native-image-configure")
        executable.text = "#!/usr/bin/env bash\necho \"$@\" > \"${logFile.absolutePath}\"\nexit 0\n"
        executable.setExecutable(true)

        def metadataCopyConfig = new MetadataCopyConfiguration()
        metadataCopyConfig.setOutputDirectory(destinationDir.absolutePath)
        metadataCopyConfig.setDisabledStages(Collections.singletonList(NativeExtension.Context.main.name()))

        def agentConfig = new AgentConfiguration()
        agentConfig.enabled = true
        agentConfig.setMetadataCopyConfiguration(metadataCopyConfig)

        def project = new MavenProject()
        def build = new Build()
        build.directory = buildDir.absolutePath
        build.outputDirectory = new File(buildDir, "classes").absolutePath
        project.build = build
        project.groupId = "org.example"
        project.artifactId = "demo"

        def mojo = new TestMetadataCopyMojo(executable)
        mojo.setAgentConfiguration(agentConfig)
        mojo.setProject(project)
        mojo.setLogger(new ConsoleLogger(Logger.LEVEL_INFO, "test"))

        when:
        mojo.execute()

        then:
        logFile.text.contains("--input-dir=${testOutput.absolutePath}")
        logFile.text.contains("--input-dir=${destinationDir.absolutePath}")
        !logFile.text.contains(mainOutput.absolutePath)
    }

    private static final class TestMetadataCopyMojo extends MetadataCopyMojo {
        private final File mergerExecutable

        TestMetadataCopyMojo(File mergerExecutable) {
            this.mergerExecutable = mergerExecutable
        }

        @Override
        File getMergerExecutable() throws MojoExecutionException {
            return mergerExecutable
        }
    }
}
