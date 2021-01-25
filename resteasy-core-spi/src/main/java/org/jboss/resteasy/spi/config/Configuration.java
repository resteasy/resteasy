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

import java.util.Optional;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface Configuration {

    /**
     * Returns the resolved value for the specified type of the named property.
     *
     * @param name the name of the parameter
     * @param type the type to convert the value to
     * @param <T>  the property type
     *
     * @return the resolved optional value
     *
     * @throws IllegalArgumentException if the type is not supported
     */
    <T> Optional<T> getOptionalValue(String name, Class<T> type);

    /**
     * Returns the resolved value for the specified type of the named property.
     *
     * @param name the name of the parameter
     * @param type the type to convert the value to
     * @param <T>  the property type
     *
     * @return the resolved value
     *
     * @throws IllegalArgumentException         if the type is not supported
     * @throws java.util.NoSuchElementException if there is no property associated with the name
     */
    <T> T getValue(String name, Class<T> type);
}
