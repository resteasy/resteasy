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

package org.jboss.resteasy.plugins.providers.jackson;

import java.util.function.Supplier;

import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.util.Functions;

/**
 * Configuration options for Jackson providers.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class JacksonOptions<T> extends Options<T> {

    /**
     * An option which allows the default {@link com.fasterxml.jackson.databind.ObjectMapper} that RESTEasy creates to
     * be disabled if no {@link jakarta.ws.rs.ext.Provider} is found for an {@link com.fasterxml.jackson.databind.ObjectMapper}.
     * <p>
     * Default is {@code false}.
     * </p>
     */
    public static final JacksonOptions<Boolean> DISABLE_DEFAULT_OBJECT_MAPPER = new JacksonOptions<>(
            "dev.resteasy.provider.jackson.disable.default.object.mapper", Boolean.class, Functions.singleton(() -> false));

    protected JacksonOptions(final String key, final Class<T> name, final Supplier<T> dftValue) {
        super(key, name, dftValue);
    }
}
