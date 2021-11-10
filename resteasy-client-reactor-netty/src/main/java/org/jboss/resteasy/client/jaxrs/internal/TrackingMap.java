package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.util.CaseInsensitiveMap;

import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;

/**
 * A decorator class to track changes to the underlying map.
 * Tracks the keys that are either added/replaced/modified in addedOrUpdatedKeys.
 * Tracks the keys that are removed in removedKeys.
 * <p>
 * Known Limitation:
 * The get and getFirst methods returns the list(since this is an instance of MultivaluedMap) value of the given key.
 * Any direct changes to the underlying list will not be tracked.
 */
public class TrackingMap<V> extends CaseInsensitiveMap<V> implements MultivaluedMap<String, V>, Cloneable {

    private final CaseInsensitiveMap<V> delegate;
    private final Set<String> addedOrUpdatedKeys;
    private final Set<String> removedKeys;

    public TrackingMap(final CaseInsensitiveMap<V> delegate) {
        this(delegate, new HashSet<>(), new HashSet<>());
    }

    private TrackingMap(final CaseInsensitiveMap<V> delegate,
                        final Set<String> addedOrUpdatedKeys,
                        final Set<String> removedKeys) {
        this.delegate = Objects.requireNonNull(delegate);
        this.addedOrUpdatedKeys = Objects.requireNonNull(addedOrUpdatedKeys);
        this.removedKeys = Objects.requireNonNull(removedKeys);
    }

    private void addToAddedOrUpdatedKeys(final String key) {
        this.removedKeys.remove(key);
        this.addedOrUpdatedKeys.add(key);
    }

    private void addToRemovedKeys(final String key) {
        this.removedKeys.add(key);
    }

    public Set<String> getAddedOrUpdatedKeys() {
        return unmodifiableSet(addedOrUpdatedKeys);
    }

    public Set<String> getRemovedKeys() {
        return unmodifiableSet(removedKeys);
    }

    @Override
    public void add(String key, V value) {
        addToAddedOrUpdatedKeys(key);
        this.delegate.add(key, value);
    }

    @Override
    public void addFirst(String key, V value) {
        addToAddedOrUpdatedKeys(key);
        this.delegate.addFirst(key, value);
    }

    @Override
    public List<V> put(String key, List<V> value) {
        addToAddedOrUpdatedKeys(key);
        return this.delegate.put(key, value);
    }

    @Override
    public void putSingle(String key, V value) {
        addToAddedOrUpdatedKeys(key);
        this.delegate.putSingle(key, value);
    }

    @Override
    public boolean replace(String key, List<V> oldValue, List<V> newValue) {
        addToAddedOrUpdatedKeys(key);
        return this.delegate.replace(key, oldValue, newValue);
    }

    @Override
    public List<V> replace(String key, List<V> value) {
        addToAddedOrUpdatedKeys(key);
        return this.delegate.replace(key, value);
    }

    @Override
    public void addAll(String key, List<V> valueList) {
        addToAddedOrUpdatedKeys(key);
        this.delegate.addAll(key, valueList);
    }

    @Override
    public void addAll(String key, V... newValues) {
        addToAddedOrUpdatedKeys(key);
        this.delegate.addAll(key, newValues);
    }

    @Override
    public List<V> merge(String key,
                         List<V> value,
                         BiFunction<? super List<V>, ? super List<V>, ? extends List<V>> remappingFunction) {
        addToAddedOrUpdatedKeys(key);
        return this.delegate.merge(key, value, remappingFunction);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<V>> t) {
        t.keySet().forEach(this::addToAddedOrUpdatedKeys);
        this.delegate.putAll(t);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super List<V>, ? extends List<V>> function) {
        this.delegate.keySet().forEach(this::addToAddedOrUpdatedKeys);
        this.delegate.replaceAll(function);
    }

    @Override
    public List<V> putIfAbsent(String key, List<V> value) {
        if (this.delegate.get(key) == null) {
            addToAddedOrUpdatedKeys(key);
        }
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public List<V> computeIfAbsent(String key, Function<? super String, ? extends List<V>> mappingFunction) {
        final List<V> value =  this.delegate.computeIfAbsent(key, mappingFunction);
        if (value != null) {
            addToAddedOrUpdatedKeys(key);
        }
        return value;
    }

    @Override
    public List<V> computeIfPresent(String key,
                                    BiFunction<? super String, ? super List<V>, ? extends List<V>> remappingFunction) {
        final List<V> value = this.delegate.computeIfPresent(key, remappingFunction);
        if (value != null) {
            addToAddedOrUpdatedKeys(key);
        }
        return value;
    }

    @Override
    public List<V> compute(String key,
                           BiFunction<? super String, ? super List<V>, ? extends List<V>> remappingFunction) {
        final List<V> value = this.delegate.compute(key, remappingFunction);
        if (value != null) {
            addToAddedOrUpdatedKeys(key);
        }
        return value;
    }

    @Override
    public List<V> remove(Object key) {
        final List<V> value = this.delegate.remove(key);
        if (value != null) {
            addToRemovedKeys(key.toString());
        }
        return value;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (this.delegate.remove(key, value)) {
            addToRemovedKeys(key.toString());
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.addedOrUpdatedKeys.clear();
        this.delegate.keySet().forEach(this.removedKeys::add);
        this.delegate.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public Set<Entry<String, List<V>>> entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    @Override
    public List<V> get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public V getFirst(String key) {
        return this.delegate.getFirst(key);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public Collection<List<V>> values() {
        return this.delegate.values();
    }

    @Override
    public boolean equalsIgnoreValueOrder(MultivaluedMap<String, V> omap) {
        return this.delegate.equalsIgnoreValueOrder(omap);
    }

    @Override
    public List<V> getOrDefault(Object key, List<V> defaultValue) {
        return this.delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super List<V>> action) {
        this.delegate.forEach(action);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public TrackingMap<V> clone() {
        final CaseInsensitiveMap<V> clone = new CaseInsensitiveMap<>();
        copy(this.delegate, clone);
        return new TrackingMap<>(
                clone,
                (Set<String>) ((HashSet<String>) this.addedOrUpdatedKeys).clone(),
                (Set<String>) ((HashSet<String>) this.removedKeys).clone()
        );
    }
}