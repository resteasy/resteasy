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

package org.jboss.resteasy.spi;

import java.util.Comparator;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

/**
 * Sorts components based on the {@link Priority @Priority. The default is {@link Priorities#USER}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class PriorityComparator<T> implements Comparator<T> {
    @Override
    public int compare(final T o1, final T o2) {
        return Integer.compare(getPriority(o1), getPriority(o2));
    }

    private int getPriority(final Object instance) {
        final Class<?> type = instance.getClass();
        return getPriority(type);
    }

    private int getPriority(final Class<?> type) {
        final Class<?> clazz = type.isSynthetic() ? type.getSuperclass() : type;
        final Priority priority = clazz.getAnnotation(Priority.class);
        if (priority != null) {
            return priority.value();
        }
        final Class<?> superType = clazz.getSuperclass();
        return superType == null ? Priorities.USER : getPriority(superType);
    }
}
