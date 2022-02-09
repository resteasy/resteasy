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

package org.jboss.resteasy.spi.util;

import java.util.function.Supplier;

/**
 * A simple utility for various functions.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Functions {

    private Functions() {
    }

    /**
     * Creates a supplier which loads the value at most once with the provided generator.
     *
     * @param generator the generator to load the value
     * @param <T>       the type of result
     *
     * @return a singleton supplier
     */
    public static <T> Supplier<T> singleton(final Supplier<T> generator) {
        return new Supplier<T>() {
            private volatile T value;

            @Override
            public T get() {
                if (value == null) {
                    synchronized (this) {
                        if (value == null) {
                            value = generator.get();
                        }
                    }
                }
                return value;
            }
        };
    }
}
