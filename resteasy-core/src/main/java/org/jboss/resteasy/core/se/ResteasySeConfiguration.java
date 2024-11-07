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

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap.Configuration;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * An implementation of the {@link Configuration}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 6.1
 */
public class ResteasySeConfiguration implements Configuration {
    private final ReadWriteLock configLock = new ReentrantReadWriteLock();
    private final Map<String, Object> properties;

    private ResteasySeConfiguration(final Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Creates a new configuration builder.
     *
     * @return the new builder
     */
    public static Configuration.Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new configuration which delegates to the configuration passed in. If the configuration does not
     * contain certain properties required by this implementation, default values are returned.
     *
     * @param configuration the delegate configuration
     *
     * @return a new configuration
     */
    public static Configuration from(final Configuration configuration) {
        return from(configuration, null);
    }

    /**
     * Creates a new configuration which delegates to the configuration passed in. If the configuration does not
     * contain certain properties required by this implementation, default values are returned.
     *
     * @param configuration   the delegate configuration
     * @param applicationPath the path from the {@link ApplicationPath#value()} annotation
     *
     * @return a new configuration
     */
    static Configuration from(final Configuration configuration, final String applicationPath) {
        if (configuration instanceof ResteasySeConfiguration) {
            if (applicationPath != null) {
                final ResteasySeConfiguration resteasyConfiguration = (ResteasySeConfiguration) configuration;
                try {
                    resteasyConfiguration.configLock.writeLock().lock();
                    if (!resteasyConfiguration.properties.containsKey(ROOT_PATH)) {
                        resteasyConfiguration.properties.put(ROOT_PATH, applicationPath);
                    }
                } finally {
                    resteasyConfiguration.configLock.writeLock().unlock();
                }
            }
            return configuration;
        }
        return new DelegateConfiguration(configuration, new Builder().build());
    }

    @Override
    public Object property(final String name) {
        final Object value;
        try {
            configLock.readLock().lock();
            value = properties.get(name);
        } finally {
            configLock.readLock().unlock();
        }
        if (value == null && name.equals(ROOT_PATH)) {
            return "/";
        }
        if (value instanceof LazyValue) {
            return ((LazyValue) value).get();
        }
        return value;
    }

    @Override
    public boolean hasProperty(final String name) {
        try {
            configLock.readLock().lock();
            return properties.containsKey(name);
        } finally {
            configLock.readLock().unlock();
        }
    }

    private static class Builder implements Configuration.Builder {
        private final Map<String, Object> properties = new ConcurrentHashMap<>();

        @Override
        public Configuration build() {
            final Map<String, Object> properties = new HashMap<>(this.properties);
            properties.putIfAbsent(PROTOCOL, ConfigurationOption.PROTOCOL.defaultValue());
            properties.computeIfAbsent(HOST, (host) -> ConfigurationOption.HOST.defaultValue());
            if (properties.containsKey(PORT)) {
                final Object portValue = properties.get(PORT);
                // Check that we're an int as it could be a LazyValue
                if (portValue instanceof Integer) {
                    final int port = (int) portValue;
                    if (port == FREE_PORT) {
                        properties.put(PORT, LazyValue.of(() -> {
                            try (ServerSocket socket = ServerSocketFactory.getDefault().createServerSocket(0)) {
                                socket.setReuseAddress(true);
                                return socket.getLocalPort();
                            } catch (IOException e) {
                                LogMessages.LOGGER.debug("Failed to discover port. Passing port 0 to implementation.", e);
                            }
                            return port;
                        }));
                    } else if (port == DEFAULT_PORT) {
                        properties.put(PORT, ConfigurationOption.PORT.defaultValue());
                    }
                } else if (!(portValue instanceof LazyValue)) {
                    throw Messages.MESSAGES.invalidArgumentType(PORT, portValue, ConfigurationOption.PORT.expectedType());
                }
            } else {
                properties.put(PORT, ConfigurationOption.PORT.defaultValue());
            }
            properties.putIfAbsent(SSL_CONTEXT, LazyValue.of(() -> {
                try {
                    return SSLContext.getDefault();
                } catch (NoSuchAlgorithmException e) {
                    throw Messages.MESSAGES.couldNotLoadSslContext(e);
                }
            }));
            properties.putIfAbsent(SSL_CLIENT_AUTHENTICATION, ConfigurationOption.SSL_CLIENT_AUTHENTICATION.defaultValue());
            return new ResteasySeConfiguration(new HashMap<>(properties));
        }

        @Override
        public Configuration.Builder property(final String name, final Object value) {
            if (value == null) {
                properties.remove(name);
            } else {
                final ConfigurationOption option = ConfigurationOption.of(name);
                if (option != null) {
                    option.validate(value);
                }
                properties.put(name, value);
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Configuration.Builder from(final BiFunction<String, Class<T>, Optional<T>> propertiesProvider) {
            for (ConfigurationOption option : ConfigurationOption.values()) {
                Optional<T> value = propertiesProvider.apply(option.key(), (Class<T>) option.expectedType());
                value.ifPresent(v -> property(option.key(), v));
            }
            return this;
        }

        @Override
        public Configuration.Builder from(final Object externalConfig) {
            if (externalConfig instanceof ResteasySeConfiguration) {
                final ResteasySeConfiguration configuration = (ResteasySeConfiguration) externalConfig;
                try {
                    configuration.configLock.readLock().lock();
                    this.properties.putAll(configuration.properties);
                } finally {
                    configuration.configLock.readLock().unlock();
                }
            } else if (externalConfig instanceof ResteasySeConfiguration.Builder) {
                this.properties.putAll(((ResteasySeConfiguration.Builder) externalConfig).properties);
            } else if (externalConfig instanceof Configuration) {
                final Configuration configuration = (Configuration) externalConfig;
                for (ConfigurationOption option : ConfigurationOption.values()) {
                    final Object value = configuration.property(option.key());
                    if (value != null) {
                        option.validate(value);
                        this.properties.put(option.key(), value);
                    }
                }
            }
            return this;
        }
    }

    private static class LazyValue {
        private final Supplier<Object> supplier;
        private volatile Object value;

        private LazyValue(final Supplier<Object> supplier) {
            this.supplier = supplier;
        }

        static LazyValue of(final Supplier<Object> supplier) {
            return new LazyValue(supplier);
        }

        Object get() {
            if (value == null) {
                synchronized (this) {
                    if (value == null) {
                        value = supplier.get();
                    }
                }
            }
            return value;
        }
    }
}
