/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.condition;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.jboss.resteasy.test.annotations.RequiresModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.wildfly.plugin.tools.VersionComparator;

/**
 * Evaluates conditions that a module exists with the minimum version, if defined.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class RequiresModuleExecutionCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getElement(), RequiresModule.class)
                .map((this::checkModule))
                .orElse(ConditionEvaluationResult
                        .enabled("Could not determine the @RequiresModule was found, enabling by default"));
    }

    private ConditionEvaluationResult checkModule(final RequiresModule requiresModule) {
        // Check if the jboss.home environment variable is set and we can attempt to use that to determine the running
        // version in the container.
        final String jbossHome = System.getProperty("jboss.home");
        // Not set, assume we need this disabled
        if (jbossHome == null) {
            return ConditionEvaluationResult.disabled("Could not find jboss.home system property.");
        }

        try {
            // Get the module XML file.
            final Optional<Path> moduleXmlFile = findModuleXml(Path.of(jbossHome)
                    .resolve("modules"), moduleToPath(requiresModule.value()));
            if (moduleXmlFile.isPresent()) {
                if (requiresModule.minVersion().isBlank()) {
                    return ConditionEvaluationResult
                            .enabled(formatReason(requiresModule, "Module %s found in %s. Enabling test.",
                                    requiresModule.value(), moduleXmlFile.get()));
                }
                return checkVersion(requiresModule, moduleXmlFile.get());
            }
        } catch (IOException e) {
            return ConditionEvaluationResult
                    .enabled("Could not find module " + requiresModule.value() + ". Enabling by default. Reason: "
                            + e.getMessage());
        }
        return ConditionEvaluationResult
                .disabled(
                        formatReason(requiresModule, "Module %s not found in %s. Disabling test.", requiresModule.value(),
                                jbossHome));
    }

    private ConditionEvaluationResult checkVersion(final RequiresModule requiresModule, final Path moduleXmlFile) {
        try {
            // Find the first instance of the resteasy-core-${version}.jar in the system modules
            final String version = version(moduleXmlFile);
            if (isAtLeastVersion(requiresModule.minVersion(), version)) {
                return ConditionEvaluationResult
                        .enabled(String.format("Found version %s and required a minimum of version %s. Enabling tests.",
                                version, requiresModule.minVersion()));
            }
            return ConditionEvaluationResult
                    .disabled(formatReason(requiresModule,
                            "Found version %s and required a minimum of version %s. Disabling test.", version,
                            requiresModule.minVersion()));
        } catch (IOException e) {
            return ConditionEvaluationResult
                    .enabled(String.format("Could not determine the version for module %s. Enabling by default. Reason: %s",
                            requiresModule.value(), e.getMessage()));
        }
    }

    private static String moduleToPath(final String moduleName) {
        return String.join(File.separator, moduleName.split("\\."));
    }

    private static String version(final Path moduleXmlFile) throws IOException {
        final Document moduleXml = Jsoup.parse(Files.readString(moduleXmlFile, StandardCharsets.UTF_8),
                Parser.xmlParser());
        final var resources = moduleXml.getElementsByTag("resources");
        // Check for a resource-root
        final var root = resources.select("resource-root");
        if (root.isEmpty()) {
            // Use the Maven GAV where the third entry should be the version
            final var name = resources.select("artifact").attr("name");
            final var gav = name.split(":");
            if (gav.length > 2) {
                return sanitizeVersion(gav[2]);
            }

        }
        final var jar = moduleXmlFile.getParent().resolve(root.attr("path"));
        try (JarFile jarFile = new JarFile(jar.toString())) {
            return extractVersionFromManifest(jarFile);
        }
    }

    private static String extractVersionFromManifest(final JarFile jarFile) throws IOException {
        final Manifest manifest = jarFile.getManifest();
        final var version = manifest.getMainAttributes().getValue("Implementation-Version");
        return sanitizeVersion(version);
    }

    private static String sanitizeVersion(final String version) {
        // Skip the "-redhat" for our purposes
        final int end = version.indexOf("-redhat");
        if (end > 0) {
            return version.substring(0, end);
        }
        return version;
    }

    private static Optional<Path> findModuleXml(final Path dir, final String pathName) throws IOException {
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter((f) -> f.toString().contains(pathName)
                    && f.getFileName().toString().equals("module.xml")).findFirst();
        }
    }

    private static boolean isAtLeastVersion(final String minVersion, final String foundVersion) {
        if (foundVersion == null) {
            return false;
        }
        return VersionComparator.compareVersion(foundVersion, minVersion) >= 0;
    }

    private static String formatReason(final RequiresModule requiresModule, final String fmt, final Object... args) {
        String msg = String.format(fmt, args);
        if (!requiresModule.issueId().isBlank()) {
            msg = requiresModule.issueId() + ": " + msg;
        }
        if (!requiresModule.reason().isBlank()) {
            msg = msg + " Reason: " + requiresModule.reason();
        }
        return msg;
    }
}
