/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.plugins.server.embedded;

import java.lang.annotation.Annotation;
import java.util.Optional;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap;

import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.PriorityServiceLoader;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * A utility for interacting with and locating {@linkplain EmbeddedServer embedded servers}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EmbeddedServers {

    /**
     * Validates a deployment is not {@code null} and {@linkplain ResteasyDeployment#start() starts} it if required.
     *
     * @param deployment the deployment to validate
     */
    public static void validateDeployment(final ResteasyDeployment deployment) {
        if (deployment == null) {
            throw Messages.MESSAGES.deploymentRequired();
        } else if (deployment.getRegistry() == null) {
            deployment.start();
        }
    }

    /**
     * Attempts to resolve the {@link ApplicationPath} on the deployments {@linkplain jakarta.ws.rs.core.Application
     * application}.
     * If the application is not set or is not annotated, {@code null} is returned.
     *
     * @param deployment the deployment to check
     *
     * @return the value of the {@link ApplicationPath} or {@code null} if the type is not annotated
     */
    public static String resolveContext(final ResteasyDeployment deployment) {
        // First check if a deployment was
        if (deployment.getApplication() != null) {
            return resolveContext(deployment.getApplication().getClass());

        }
        if (deployment.getApplicationClass() != null) {
            try {
                final Class<?> clazz = Class.forName(deployment.getApplicationClass());
                return resolveContext(clazz);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

    /**
     * Attempts to resolve the {@link ApplicationPath} on the type. If the type is not annotated, {@code null} is
     * returned.
     *
     * @param type the type to check
     *
     * @return the value of the {@link ApplicationPath} or {@code null} if the type is not annotated
     */
    public static String resolveContext(final Class<?> type) {
        final ApplicationPath applicationPath = findAnnotation(ApplicationPath.class, type);
        return applicationPath == null ? null : applicationPath.value();
    }

    /**
     * Checks the context path and if required creates a new path with a leading {@code /}.
     * <p>
     * The deployments application is checked for a {@link ApplicationPath}. If application is not set or is not
     * annotated with {@link ApplicationPath}, then {@code "/"} is returned.
     * </p>
     *
     * @param deployment the deployment to check
     *
     * @return the context path with a leading {@code /} if required
     */
    public static String checkContextPath(final ResteasyDeployment deployment) {
        return checkContextPath(resolveContext(deployment));
    }

    /**
     * Checks the context path and if required creates a new path with a leading {@code /}.
     *
     * @param contextPath the context path to check
     *
     * @return the context path with a leading {@code /} if required
     */
    public static String checkContextPath(final String contextPath) {
        if (contextPath == null || contextPath.isBlank()) {
            return "/";
        } else if (contextPath.charAt(0) != '/') {
            return "/" + contextPath;
        }
        return contextPath;
    }

    /**
     * Attempts to find the server first via a {@linkplain PriorityServiceLoader service loader}.
     *
     * @return the embedded server found
     */
    public static EmbeddedServer findServer() {
        return findServer(null);
    }

    /**
     * Attempts to find the server first in the {@linkplain SeBootstrap.Configuration configuration}, then via a
     * {@linkplain PriorityServiceLoader service loader}.
     *
     * @param configuration the configuration to attempt to locate the server in
     *
     * @return the embedded server found
     */
    public static EmbeddedServer findServer(final SeBootstrap.Configuration configuration) {
        if (configuration != null && configuration.hasProperty(ConfigurationOption.EMBEDDED_SERVER.key())) {
            final Object instance = ConfigurationOption.EMBEDDED_SERVER.getValue(configuration);
            if (instance instanceof EmbeddedServer) {
                return (EmbeddedServer) instance;
            }
            LogMessages.LOGGER.invalidPropertyType(instance, ConfigurationOption.EMBEDDED_SERVER.key(),
                    EmbeddedServer.class.getName());
        }
        final Optional<EmbeddedServer> found = PriorityServiceLoader.load(EmbeddedServer.class)
                .first();
        return found.orElseThrow(() -> Messages.MESSAGES.noImplementationFound(EmbeddedServer.class.getName()));
    }

    private static <T extends Annotation> T findAnnotation(final Class<T> annotation, final Class<?> type) {
        if (type == null) {
            return null;
        }
        final T result = type.getAnnotation(annotation);
        if (result == null) {
            return findAnnotation(annotation, type.getSuperclass());
        }
        return result;
    }
}
