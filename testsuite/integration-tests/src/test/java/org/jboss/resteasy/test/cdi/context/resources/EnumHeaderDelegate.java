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

package org.jboss.resteasy.test.cdi.context.resources;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("unchecked")
@Provider
public class EnumHeaderDelegate implements RuntimeDelegate.HeaderDelegate<Enum<?>> {
    @Override
    public Enum<?> fromString(final String value) {
        // Find the last index of the dot which separates the class name and the enum name
        final int nameIndex = value.lastIndexOf('.');
        final String className = value.substring(0, nameIndex);
        final String enumName = value.substring(nameIndex + 1);
        // Attempt to create the enum class
        final Class<? extends Enum<?>> enumClass;
        try {
            enumClass = (Class<? extends Enum<?>>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(e);
        }
        return resolveEnumValue(enumClass, enumName);
    }

    @Override
    public String toString(final Enum<?> value) {
        return String.format("%s.%s", value.getClass().getName(), value.name());
    }

    private static <T extends Enum<T>> T resolveEnumValue(final Class<?> enumType, final String name) {
        return Enum.valueOf((Class<T>) enumType, name);
    }
}
