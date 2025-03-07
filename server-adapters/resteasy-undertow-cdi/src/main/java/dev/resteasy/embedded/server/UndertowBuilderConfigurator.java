/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2025 Red Hat, Inc., and individual contributors
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
package dev.resteasy.embedded.server;

import io.undertow.Undertow;

/**
 * This interface is discovered as a {@linkplain org.jboss.resteasy.spi.PriorityServiceLoader service provider} for
 * configuring the {@link Undertow.Builder} before it builds the {@linkplain Undertow server} and starts it.
 * <p>
 * Implementations of this service will be executed after the {@link Undertow.Builder} is initialized, but before the
 * server starts. You can optionally use the {@link jakarta.annotation.Priority} annotation for ordered execution.
 * </p>
 */
public interface UndertowBuilderConfigurator {

    /**
     * Allows configuring a {@link Undertow.Builder} before the {@linkplain Undertow server} is built.
     *
     * @param builder the builder used for configuring the server
     */
    void configure(Undertow.Builder builder);
}
