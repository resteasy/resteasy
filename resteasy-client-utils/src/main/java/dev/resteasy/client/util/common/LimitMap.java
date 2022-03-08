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

package dev.resteasy.client.util.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import dev.resteasy.client.util.logging.ClientMessages;

/**
 * A simple map which limits the number of entries allowed.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LimitMap<K, V> implements Map<K, V> {
    private final Object lock = new Object();
    private final Map<K, V> delegate;

    /**
     * Creates a map which limits the entries.
     *
     * @param limit the maximum amount of entries allowed, must be greater than 0
     */
    public LimitMap(final int limit) {
        if (limit < 1) {
            throw ClientMessages.MESSAGES.invalidValue(0, limit);
        }
        delegate = new LinkedHashMap<>(limit) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                synchronized (lock) {
                    return size() > limit;
                }
            }
        };
    }

    /**
     * Creates a map which limits the entries.
     *
     * @param limit the maximum amount of entries allowed, must be greater than 0
     */
    public static <K, V> LimitMap<K, V> of(final int limit) {
        return new LimitMap<>(limit);
    }

    @Override
    public int size() {
        synchronized (lock) {
            return delegate.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return delegate.isEmpty();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        synchronized (lock) {
            return delegate.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        synchronized (lock) {
            return delegate.containsValue(value);
        }
    }

    @Override
    public V get(final Object key) {
        synchronized (lock) {
            return delegate.get(key);
        }
    }

    @Override
    public V put(final K key, final V value) {
        synchronized (lock) {
            return delegate.put(key, value);
        }
    }

    @Override
    public V remove(final Object key) {
        synchronized (lock) {
            return delegate.remove(key);
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        synchronized (lock) {
            delegate.putAll(m);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            delegate.clear();
        }
    }

    @Override
    public Set<K> keySet() {
        synchronized (lock) {
            return new LinkedHashSet<>(delegate.keySet());
        }
    }

    @Override
    public Collection<V> values() {
        synchronized (lock) {
            return new ArrayList<>(delegate.values());
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        synchronized (lock) {
            return new LinkedHashSet<>(delegate.entrySet());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LimitMap)) {
            return false;
        }
        synchronized (lock) {
            return Objects.equals(delegate, ((LimitMap<K, V>) o).delegate);
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return Objects.hash(delegate);
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        synchronized (lock) {
            return delegate.getOrDefault(key, defaultValue);
        }
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        synchronized (lock) {
            delegate.forEach(action);
        }
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        synchronized (lock) {
            delegate.replaceAll(function);
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        synchronized (lock) {
            return delegate.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        synchronized (lock) {
            return delegate.remove(key, value);
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        synchronized (lock) {
            return delegate.replace(key, oldValue, newValue);
        }
    }

    @Override
    public V replace(final K key, final V value) {
        synchronized (lock) {
            return delegate.replace(key, value);
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        synchronized (lock) {
            return delegate.computeIfAbsent(key, mappingFunction);
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return delegate.computeIfPresent(key, remappingFunction);
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return delegate.compute(key, remappingFunction);
        }
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        synchronized (lock) {
            return delegate.merge(key, value, remappingFunction);
        }
    }
}
