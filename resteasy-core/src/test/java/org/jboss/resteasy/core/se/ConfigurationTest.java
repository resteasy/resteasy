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
import java.util.Optional;

import jakarta.ws.rs.SeBootstrap.Configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ConfigurationTest {

    @Test
    public void correctConfigurator() {
        Assertions.assertTrue(Configuration.builder().build() instanceof ResteasySeConfiguration);
    }

    @Test
    public void fromFunction() {
        final Configuration.Builder builder = Configuration.builder()
                .from((key, type) -> {
                    if (type.isAssignableFrom(Integer.class)) {
                        return Optional.of(9999);
                    } else if (key.equals(Configuration.HOST)) {
                        return Optional.of(":::1");
                    } else if (key.equals(Configuration.PROTOCOL)) {
                        return Optional.of("https");
                    }
                    return Optional.empty();
                });

        final Configuration configuration = builder.build();
        Assertions.assertEquals(9999, configuration.port());
        Assertions.assertEquals(":::1", configuration.host());
        Assertions.assertEquals("https", configuration.protocol());
    }

    @Test
    public void fromImplConfigurationBuilder() {
        final Configuration.Builder builder = Configuration.builder()
                .from(Configuration.builder().property("test.from.config", "value set")
                        .property(Configuration.PORT, 9999)
                        .property(ConfigurationOption.HOST.key(), ":::1"));

        final Configuration configuration = builder.build();
        Assertions.assertEquals(9999, configuration.port());
        Assertions.assertEquals(":::1", configuration.host());
        Assertions.assertEquals("value set", configuration.property("test.from.config"));
    }

    @Test
    public void fromImplConfiguration() {
        final Configuration.Builder builder = Configuration.builder()
                .from(Configuration.builder().property("test.from.config", "value set")
                        .property(Configuration.PORT, 8443)
                        .property(ConfigurationOption.HOST.key(), ":::1")
                        .build());

        final Configuration configuration = builder.build();
        Assertions.assertEquals(8443, configuration.port());
        Assertions.assertEquals(":::1", configuration.host());
        Assertions.assertEquals("value set", configuration.property("test.from.config"));
    }

    @Test
    public void fromConfiguration() {
        final Configuration.Builder builder = Configuration.builder()
                .from(TestConfiguration.create().property("test.from.config", "value set")
                        .property(Configuration.PORT, 8443)
                        .property(ConfigurationOption.HOST.key(), ":::1"));

        final Configuration configuration = builder.build();
        Assertions.assertEquals(8443, configuration.port());
        Assertions.assertEquals(":::1", configuration.host());
        Assertions.assertNull(configuration.property("test.from.config"),
                () -> "Expected test.from.config to not be defined");
    }

    @Test
    public void rootPathNotSet() {
        final Configuration.Builder builder = Configuration.builder();
        Assertions.assertEquals("/", builder.build().rootPath());
    }

    @Test
    public void rootPathSet() {
        final Configuration.Builder builder = Configuration.builder()
                .rootPath("root-path");
        Assertions.assertEquals("root-path", builder.build().rootPath());
    }

    private static class TestConfiguration implements Configuration {
        private final Map<String, Object> properties = new HashMap<>();

        static TestConfiguration create() {
            return new TestConfiguration();
        }

        TestConfiguration property(final String name, final Object value) {
            properties.put(name, value);
            return this;
        }

        @Override
        public Object property(final String name) {
            return properties.get(name);
        }
    }
}
