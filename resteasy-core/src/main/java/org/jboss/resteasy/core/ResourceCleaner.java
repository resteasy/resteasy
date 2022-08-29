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

package org.jboss.resteasy.core;

import java.lang.ref.Cleaner;

/**
 * A utility used to register resources to be cleaned with a {@link Cleaner}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResourceCleaner {

    private static final Cleaner CLEANER = Cleaner.create();

    private ResourceCleaner() {
    }

    /**
     * Register an object and an action to clean up resources associated with the object once the object instance
     * becomes phantom unreachable.
     *
     * @param instance the instance to monitor
     * @param action   the action to run for the clean up
     *
     * @return a cleanable instance
     *
     * @see Cleaner
     */
    public static Cleaner.Cleanable register(final Object instance, final Runnable action) {
        return CLEANER.register(instance, action);
    }
}
