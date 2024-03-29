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

package org.jboss.resteasy.core;

import java.util.Objects;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.config.Configuration;
import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * Describes basic information about an {@link Application}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ApplicationDescription {

    private final Class<? extends Application> type;
    private final Application instance;
    private final String path;

    private ApplicationDescription(final Class<? extends Application> type, final Application instance, final String path) {
        this.type = type;
        this.instance = instance;
        this.path = path;
    }

    /**
     * Returns the class of the application.
     *
     * @return the class for the application
     */
    public Class<? extends Application> type() {
        return type;
    }

    /**
     * Returns the instance of the application.
     *
     * @return the instance of the application
     */
    public Application instance() {
        return instance;
    }

    /**
     * Returns the path of the application. An empty path is always represented as {@code /}.
     *
     * @return the path of the application
     */
    public String path() {
        return path;
    }

    /**
     * Builds an application description.
     */
    public static class Builder {
        private final Application application;
        private Class<? extends Application> type;
        private String path;

        private Builder(final Application application) {
            this.application = application;
        }

        /**
         * Creates a new build based on the application.
         *
         * @param application the application to create the description for, cannot be {@code null}
         *
         * @return the builder
         */
        public static Builder of(final Application application) {
            return new Builder(Objects.requireNonNull(application, () -> Messages.MESSAGES.nullParameter("application")));
        }

        /**
         * Defines the type for the application. If set to {@code null}, the type will be resolved from the
         * {@linkplain Application#getClass() application}.
         *
         * @param type the applications class
         *
         * @return the builder
         */
        public Builder type(final Class<? extends Application> type) {
            this.type = type;
            return this;
        }

        /**
         * Defines the path of the application. If set to {@code null}, the path is resolved from the
         * {@link ApplicationPath}. If the application is not annotated, an attempt to look up the defined mapping is
         * done. If neither can be found, the assumed path is {@code /}.
         *
         * @param path the path for the application
         *
         * @return the builder
         */
        public Builder path(final String path) {
            this.path = path;
            return this;
        }

        /**
         * Builds the application description.
         *
         * @return the application description
         */
        public ApplicationDescription build() {
            if (type == null) {
                type = application.getClass();
            }
            if (path == null) {
                final ApplicationPath applicationPath = type.getAnnotation(ApplicationPath.class);
                if (applicationPath != null) {
                    path = applicationPath.value();
                    if (path.isBlank()) {
                        path = "/";
                    }
                } else {
                    // Check for a servlet mapping name
                    final Configuration configuration = ConfigurationFactory.getInstance().getConfiguration();
                    final var mapping = configuration
                            .getOptionalValue(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, String.class);
                    path = mapping.filter((v) -> !v.isBlank()).orElse("/");
                }
            }
            return new ApplicationDescription(type, application, path);
        }
    }
}
