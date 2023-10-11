/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.spi.config;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ConfigurationTestCase {

    private static final Set<Property<?>> EXPECTED_CONVERSIONS = new HashSet<>();

    private enum TestEnum {
        TEST
    }

    @BeforeAll
    public static void configureProperties() {
        EXPECTED_CONVERSIONS.add(Property.of("test.string", String.class, "test string value"));
        EXPECTED_CONVERSIONS.add(Property.of("test.Boolean", Boolean.class, Boolean.TRUE));
        EXPECTED_CONVERSIONS.add(Property.of("test.boolean", boolean.class, true));
        EXPECTED_CONVERSIONS.add(Property.of("test.byte", byte.class, (byte) 98));
        EXPECTED_CONVERSIONS.add(Property.of("test.Byte", Byte.class, Byte.valueOf("98")));
        EXPECTED_CONVERSIONS.add(Property.of("test.short", short.class, Short.valueOf("5")));
        EXPECTED_CONVERSIONS.add(Property.of("test.Short", Short.class, Short.parseShort("6")));
        EXPECTED_CONVERSIONS.add(Property.of("test.int", int.class, 10));
        EXPECTED_CONVERSIONS.add(Property.of("test.Integer", Integer.class, 11));
        EXPECTED_CONVERSIONS.add(Property.of("test.long", long.class, 15L));
        EXPECTED_CONVERSIONS.add(Property.of("test.Long", Long.class, Long.valueOf("16")));
        EXPECTED_CONVERSIONS.add(Property.of("test.float", float.class, 5.0f));
        EXPECTED_CONVERSIONS.add(Property.of("test.Float", Float.class, 5.5f));
        EXPECTED_CONVERSIONS.add(Property.of("test.double", double.class, 10.0d));
        EXPECTED_CONVERSIONS.add(Property.of("test.Double", Double.class, 10.5d));
        EXPECTED_CONVERSIONS.add(Property.of("test.big.decimal", BigDecimal.class, new BigDecimal("20.00")));
        EXPECTED_CONVERSIONS.add(Property.of("test.enum", TestEnum.class, TestEnum.TEST));

        for (Property<?> property : EXPECTED_CONVERSIONS) {
            TestResteasyConfiguration.PROPS.put(property.name, String.valueOf(property.expectedValue));
        }
    }

    @Test
    public void testDefaultConfiguration() {
        final Configuration configuration = new DefaultConfiguration();

        Assertions.assertTrue(configuration.getOptionalValue("test.config.prop", String.class).isPresent(),
                () -> "Expected the system property test.config.prop to be present");

        Assertions.assertTrue(configuration.getOptionalValue("TEST_CONFIG_ENV", String.class).isPresent(),
                () -> "Expected the environment variable TEST_CONFIG_ENV tobe present");

        Assertions.assertFalse(configuration.getOptionalValue("test.config.invalid", String.class).isPresent(),
                () -> "Did not expect the property test.config.invalid to exist");

        Assertions.assertEquals("sys-prop-value", configuration.getValue("test.config.prop", String.class),
                () -> "Expected the system property value sys-prop-value");

        Assertions.assertEquals("env-value",
                configuration.getValue("TEST_CONFIG_ENV", String.class),
                () -> "Expected the environment variable value env-value");

        Assertions.assertThrows(NoSuchElementException.class, () -> configuration.getValue("test.config.invalid", String.class),
                () -> "Expected a NoSuchElementException");

    }

    @Test
    public void testConfigurationFactory() {
        Assertions.assertEquals(TestConfigurationFactory.class, ConfigurationFactory.getInstance().getClass());
    }

    @Test
    public void testConversions() {
        final Configuration configuration = ConfigurationFactory.getInstance().getConfiguration();

        for (Property<?> property : EXPECTED_CONVERSIONS) {
            Assertions.assertEquals(property.expectedValue, configuration.getValue(property.name, property.type));
            final Optional<?> optional = configuration.getOptionalValue(property.name, property.type);
            Assertions.assertTrue(optional.isPresent(), String.format("Expected property %s to be present", property.name));
            Assertions.assertEquals(property.expectedValue, optional.get());
        }
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> configuration.getOptionalValue("test.string", Date.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> configuration.getValue("test.string", Date.class));
    }

    private static class Property<T> {
        final String name;
        final Class<T> type;
        final T expectedValue;

        private Property(final String name, final Class<T> type, final T expectedValue) {
            this.name = name;
            this.type = type;
            this.expectedValue = expectedValue;
        }

        static <T> Property<T> of(final String name, final Class<T> type, final T expectedValue) {
            return new Property<>(name, type, expectedValue);
        }
    }
}
