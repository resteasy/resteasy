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

package org.jboss.resteasy.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Priority;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.util.Functions;

/**
 * <p>
 * A service loader which loads classes aggressively sorting the implementations by the value in the {@link Priority}
 * annotation. If the implementation does not include the annotation {@link Integer#MAX_VALUE} is used for the priority.
 * The instances themselves are lazily created.
 * </p>
 * <br/>
 * <p>
 * <h3><a id="constructorArg">Constructor</a></h3>
 * If a {@linkplain Function constructor function} is used the argument passed to the function is the resolved type.
 * The function is free to construct the type however it sees fit.
 * </p>
 * <p>
 * Example:
 *
 * <pre>{@code
 * PriorityServiceLoader.load(Service.class, (service) -> {
 *     try {
 *         return service.getConstructor(String.class).newInstance("init value");
 *     } catch (Exception e) {
 *         throw new RuntimeException(e);
 *     }
 * });
 * }</pre>
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see java.util.ServiceLoader
 * @since 6.1
 */
public class PriorityServiceLoader<S> implements Iterable<S> {

    private static final String PREFIX = "META-INF/services/";

    private final Object lock = new Object();
    private final Supplier<String> toString;
    // Guarded by lock
    private final Holder<S>[] holders;
    private final int size;

    private PriorityServiceLoader(final Class<S> service, final Holder<S>[] holders) {
        this.holders = holders;
        size = holders.length;
        toString = Functions.singleton(() -> "PriorityServiceLoader[type=" + service.getName()
                + ", implementations=" + Stream.of(holders)
                        .map((holder) -> holder.type.getName())
                        .collect(Collectors.toList())
                + "]");
    }

    /**
     * Creates a new service loader for the type.
     * <p>
     * To resolve the class loader this first attempts to get the {@linkplain Thread#currentThread() current threads}
     * {@linkplain Thread#getContextClassLoader() context class loader}. If that is {@code null} the services class
     * loader is used. Finally if the class loader from the service is {@code null} then the
     * {@linkplain ClassLoader#getSystemClassLoader() system class loader} is used.
     * </p>
     *
     * @param type the type to load the services for
     *
     * @return a new service loader
     *
     * @throws SecurityException if the security manager is enabled there is a security issue loading the class or
     *                           retrieving the class loader
     */
    public static <S> PriorityServiceLoader<S> load(final Class<S> type) {
        return load(type, classLoader(type), null);
    }

    /**
     * Creates a new service loader for the type.
     * <p>
     * To resolve the class loader this first attempts to get the {@linkplain Thread#currentThread() current threads}
     * {@linkplain Thread#getContextClassLoader() context class loader}. If that is {@code null} the services class
     * loader is used. Finally if the class loader from the service is {@code null} then the
     * {@linkplain ClassLoader#getSystemClassLoader() system class loader} is used.
     * </p>
     *
     * @param type        the type to load the services for
     * @param constructor an optional <a href="#constructorArg">constructor</a> used to construct the type
     *
     * @return a new service loader
     *
     * @throws SecurityException if the security manager is enabled there is a security issue loading the class or
     *                           retrieving the class loader
     */
    public static <S> PriorityServiceLoader<S> load(final Class<S> type,
            final Function<Class<? extends S>, S> constructor) {
        return load(type, classLoader(type), constructor);
    }

    /**
     * Creates a new service loader for the type and class loader.
     *
     * @param type the type to load the services for
     * @param cl   the class loader used to load the found services
     *
     * @return a new service loader
     *
     * @throws SecurityException if the security manager is enabled and there is a security issue loading the class
     */
    public static <S> PriorityServiceLoader<S> load(final Class<S> type, final ClassLoader cl) {
        return load(type, cl, null);
    }

    /**
     * Creates a new service loader for the type and class loader.
     *
     * @param type        the type to load the services for
     * @param cl          the class loader used to load the found services
     * @param constructor an optional <a href="#constructorArg">constructor</a> used to construct the type
     *
     * @return a new service loader
     *
     * @throws SecurityException if the security manager is enabled and there is a security issue loading the class
     */
    public static <S> PriorityServiceLoader<S> load(final Class<S> type, final ClassLoader cl,
            final Function<Class<? extends S>, S> constructor) {
        try {
            final Holder<S>[] holders = findClasses(type, cl, constructor);
            return new PriorityServiceLoader<>(type, holders);
        } catch (IOException e) {
            throw Messages.MESSAGES.failedToLoadService(e, type);
        }
    }

    /**
     * If there are services available the first one is returned.
     *
     * @return the first service or an empty optional
     *
     * @throws SecurityException if the security manager is enabled and there is a security error instantiating the
     *                           object
     */
    public Optional<S> first() {
        if (size > 0) {
            synchronized (lock) {
                return Optional.of(holders[0].getInstance());
            }
        }
        return Optional.empty();
    }

    /**
     * If there are services available the last one is returned.
     *
     * @return the last service or an empty optional
     *
     * @throws SecurityException if the security manager is enabled and there is a security error instantiating the
     *                           object
     */
    public Optional<S> last() {
        if (size > 0) {
            synchronized (lock) {
                return Optional.of(holders[size - 1].getInstance());
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the types found for this service.
     * <p>
     * Note if accessed before the iterator the types are not actually constructed.
     * </p>
     *
     * @return the types found for this service
     */
    public Set<Class<S>> getTypes() {
        final Holder<S>[] holders;
        synchronized (lock) {
            holders = this.holders.clone();
        }
        final int len = holders.length;
        final Set<Class<S>> result = new LinkedHashSet<>(len);
        for (Holder<S> holder : holders) {
            result.add(holder.type);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @throws SecurityException if the security manager is enabled and there is a security error instantiating the
     *                           object
     */
    @Override
    public Iterator<S> iterator() {
        final Holder<S>[] holders;
        synchronized (lock) {
            holders = this.holders.clone();
        }
        return new Iterator<>() {
            final AtomicInteger current = new AtomicInteger();

            @Override
            public boolean hasNext() {
                final int i = current.get();
                return (i + 1) <= size;
            }

            @Override
            public S next() {
                final int i = current.getAndIncrement();
                if (i >= size) {
                    throw new NoSuchElementException();
                }
                return holders[i].getInstance();
            }
        };
    }

    @Override
    public String toString() {
        return toString.get();
    }

    @SuppressWarnings("unchecked")
    private static <S> Holder<S>[] findClasses(final Class<S> type, final ClassLoader cl,
            final Function<Class<? extends S>, S> constructor) throws IOException {
        final Set<Holder<S>> holders = new TreeSet<>();
        final Enumeration<URL> resources = cl.getResources(PREFIX + type.getName());
        while (resources.hasMoreElements()) {
            final URL url = resources.nextElement();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int commentIdx = line.indexOf('#');
                    if (commentIdx >= 0) {
                        line = line.substring(0, commentIdx);
                    }
                    line = line.trim();
                    if (line.equals(""))
                        continue;
                    try {
                        final Class<S> found = (Class<S>) cl.loadClass(line);
                        final Priority priority = found.getAnnotation(Priority.class);
                        int p = Integer.MAX_VALUE;
                        if (priority != null) {
                            p = priority.value();
                        }
                        holders.add(new Holder<>(found, p, constructor));
                    } catch (ClassNotFoundException e) {
                        LogMessages.LOGGER.failedToLoad(e, line);
                    }
                }
            } catch (IOException e) {
                LogMessages.LOGGER.failedToLoad(e, url.toString());
            }
        }
        return (Holder<S>[]) holders.toArray(new Holder[0]);
    }

    private static ClassLoader classLoader(final Class<?> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = service.getClassLoader();
        }
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    private static class Holder<S> implements Comparable<Holder<S>> {
        final Class<S> type;
        final int priority;
        final Function<Class<? extends S>, S> constructor;
        volatile S instance;

        private Holder(final Class<S> type, final int priority, final Function<Class<? extends S>, S> constructor) {
            this.type = type;
            this.priority = priority;
            this.constructor = constructor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Holder)) {
                return false;
            }
            final Holder<?> other = (Holder<?>) obj;
            return Objects.equals(type, other.type);
        }

        @Override
        public int compareTo(final Holder o) {
            return Integer.compare(priority, o.priority);
        }

        @Override
        public String toString() {
            return "Holder[type=" + type + ", priority=" + priority + ", currentInstance=" + instance + "]";
        }

        S getInstance() {
            if (instance == null) {
                synchronized (this) {
                    try {
                        instance = createInstance();
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                            | IllegalAccessException e) {
                        throw Messages.MESSAGES.failedToConstructClass(e, type);
                    }
                }
            }
            return instance;
        }

        private S createInstance()
                throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            if (constructor == null) {
                return type.getConstructor().newInstance();
            }
            return constructor.apply(type);
        }
    }
}
