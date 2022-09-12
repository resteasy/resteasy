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

package org.jboss.resteasy.core.se;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.SeBootstrap.Configuration.SSLClientAuthentication;

import org.jboss.jandex.Index;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedServer;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.PortProvider;

/**
 * Configurations options for configuring an {@link jakarta.ws.rs.SeBootstrap.Instance}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("unchecked")
public enum ConfigurationOption {

    PROTOCOL(Configuration.PROTOCOL, "HTTP", String.class) {
        @Override
        public void validate(final Object value) {
            if (!(value instanceof String)) {
                throw Messages.MESSAGES.invalidProtocol(String.valueOf(value), "HTTP", "HTTPS");
            }
            final String protocol = (String) value;
            if (!("HTTP".equalsIgnoreCase(protocol) || "HTTPS".equalsIgnoreCase(protocol))) {
                throw Messages.MESSAGES.invalidProtocol(protocol, "HTTP", "HTTPS");
            }
        }
    },
    HOST(Configuration.HOST, PortProvider.getHost(), String.class),
    PORT(Configuration.PORT, PortProvider.getPort(), Integer.class),
    ROOT_PATH(Configuration.ROOT_PATH, "/", String.class),
    SSL_CONTEXT(Configuration.SSL_CONTEXT, null, SSLContext.class),
    SSL_CLIENT_AUTHENTICATION(Configuration.SSL_CLIENT_AUTHENTICATION, SSLClientAuthentication.NONE, SSLClientAuthentication.class),
    EMBEDDED_SERVER(
            "org.jboss.resteasy.se.embedded.server.instance", null, EmbeddedServer.class),
    JANDEX_INDEX("org.jboss.resteasy.jandex.index", null, Index.class),
    JANDEX_CLASS_PATH_FILTER("org.jboss.resteasy.jandex.filter", null, Predicate.class),
    REGISTER_BUILT_INS(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS, true, Boolean.class),
    ;

    private static final Map<String, ConfigurationOption> LOOKUP = new HashMap<>();

    static {
        for (ConfigurationOption o : values()) {
            LOOKUP.put(o.key(), o);
        }
    }

    private final String name;
    private final Object value;
    private final Class<?> expectedType;

    ConfigurationOption(final String name, final Object value, final Class<?> expectedType) {
        this.name = name;
        this.value = value;
        this.expectedType = expectedType;
    }

    public static ConfigurationOption of(final String key) {
        return LOOKUP.get(key);
    }

    /**
     * Resolves the value from the configuration
     *
     * @param configuration the configuration the value is resolved from
     * @param <T>           the type for the value
     *
     * @return the value or the default value which may be {@code null}
     */
    public <T> T getValue(final Configuration configuration) {
        if (configuration.hasProperty(name)) {
            return (T) configuration.property(name);
        }
        return (T) value;
    }

    /**
     * The key for the property.
     *
     * @return the key for the property
     */
    public String key() {
        return name;
    }

    /**
     * The default value.
     *
     * @param <T> the type of the value
     *
     * @return the default value
     */
    public <T> T defaultValue() {
        return (T) expectedType.cast(value);
    }

    /**
     * The expected type.
     *
     * @param <T> the type of the value
     *
     * @return the expected type
     */
    public <T> Class<? extends T> expectedType() {
        return (Class<? extends T>) expectedType;
    }

    /**
     * Validates the value can be assigned to this configuration option.
     *
     * @param value the value to validate
     *
     * @throws IllegalArgumentException if the value cannot be assigned to this configuration option
     */
    public void validate(final Object value) {
        if (!expectedType.isInstance(value)) {
            throw Messages.MESSAGES.invalidArgumentType(name, value, expectedType);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
