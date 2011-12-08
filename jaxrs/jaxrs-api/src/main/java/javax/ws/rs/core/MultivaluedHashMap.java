/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An implementation of {@link MultivaluedMap} on top of a {@link HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Paul Sandoz
 * @author Marek Potociar
 *
 * @since 2.0
 */
public class MultivaluedHashMap<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V> {

    private static final long serialVersionUID = -6052320403766368902L;

    /**
     * Constructs an empty multivalued hash map with initial capacity and load
     * factor set to {@link HashMap} defaults.
     *
     * @see HashMap#HashMap()
     */
    public MultivaluedHashMap() {
    }

    /**
     * Constructs an empty multivalued hash map with the specified initial
     * capacity and the default load factor.
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     *
     * @see HashMap#HashMap(int)
     */
    public MultivaluedHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty multivalued hash map with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     *
     * @see HashMap#HashMap(int, float)
     */
    public MultivaluedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new multivalued hash map with the same mappings as the
     * specified {@link MultivaluedMap }. The {@link List} instances holding
     * the values of each key are created anew instead of being reused.
     *
     * @param  map the multivalued map whose mappings are to be placed in this
     *     multivalued map.
     * @throws NullPointerException if the specified map is {@code null}
     */
    public MultivaluedHashMap(MultivaluedMap<? extends K, ? extends V> map) {
        putAll(map);
    }

    /**
     * This private method is used by the copy constructor to avoid exposing
     * additional generic parameters through the public API documentation.
     *
     * @param <T> any subclass of K
     * @param <U> any subclass of V
     * @param map the map
     */
    private <T extends K, U extends V> void putAll(MultivaluedMap<T, U> map) {
        for (Entry<T, List<U>> e : map.entrySet()) {
            this.put(e.getKey(), new ArrayList<V>(e.getValue()));
        }
    }


    /**
     * Constructs a new multivalued hash map with the same mappings as the
     * specified single-valued {@link Map }.
     *
     * @param  map the single-valued map whose mappings are to be placed in this
     *     multivalued map.
     * @throws NullPointerException if the specified map is {@code null}
     */
    public MultivaluedHashMap(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            this.putSingle(e.getKey(), e.getValue());
        }
    }

    /**
     * Set the value for the key to be a one item list consisting of the supplied
     * value. Any existing values will be replaced.
     * <p />
     * NOTE: This implementation ignores {@code null} values; A supplied value
     * of {@code null} is ignored and not added to the purged value list.
     * As a result of such operation, empty value list would  be registered for
     * the supplied key. Overriding implementations may modify this behavior by
     * redefining the {@link #addNull(java.util.List)} method.
     *
     * @param key the key
     * @param value the single value of the key. If the value is {@code null} it
     *     will be ignored.
     */
    @Override
    public final void putSingle(K key, V value) {
        List<V> values = getValues(key);

        values.clear();
        if (value != null) {
            values.add(value);
        } else {
            addNull(values);
        }
    }

    /**
     * Define the behavior for adding a {@code null} values to the value list.
     * <p />
     * Default implementation is a no-op, i.e. the {@code null} values are ignored.
     * Overriding implementations may modify this behavior by providing their
     * own definitions of this method.
     *
     * @param values value list where the {@code null} value addition is being
     *     requested.
     */
    protected void addNull(List<V> values) {
        // do nothing in the default implementation; ignore the null value
    }

    /**
     * Define the behavior for adding a {@code null} values to the first position
     * in the value list.
     * <p />
     * Default implementation is a no-op, i.e. the {@code null} values are ignored.
     * Overriding implementations may modify this behavior by providing their
     * own definitions of this method.
     *
     * @param values value list where the {@code null} value addition is being
     *     requested.
     */
    protected void addFirstNull(List<V> values) {
        // do nothing in the default implementation; ignore the null value
    }

    @Override
    /**
     * Add a value to the current list of values for the supplied key.
     * <p />
     * NOTE: This implementation ignores {@code null} values; A supplied value
     * of {@code null} is ignored and not added to the value list. Overriding
     * implementations may modify this behavior by redefining the
     * {@link #addNull(java.util.List)} method.
     *
     * @param key the key
     * @param value the value to be added.
     */
    public final void add(K key, V value) {
        List<V> values = getValues(key);

        if (value != null) {
            values.add(value);
        } else {
            addNull(values);
        }
    }
    
    /**
     * Add multiple values to the current list of values for the supplied key. If 
     * the supplied array of new values is empty, method returns immediately. 
     * Method throws a {@code NullPointerException} if the supplied array of values 
     * is {@code null}.
     * <p />
     * NOTE: This implementation ignores {@code null} values; Any of the supplied values
     * of {@code null} is ignored and not added to the value list. Overriding
     * implementations may modify this behavior by redefining the
     * {@link #addNull(java.util.List)} method.
     *
     * @param key the key.
     * @param newValues the values to be added.
     * @throws NullPointerException if the supplied array of new values is {@code null}.
     */
    public final void addAll(K key, V... newValues) {
        if (newValues == null || newValues.length == 0) {
            return;
        }
        
        List<V> values = getValues(key);

        for (V value : newValues) {
            if (value != null) {
                values.add(value);
            } else {
                addNull(values);
            }            
        }        
    }

    /**
     * Add all the values from the supplied value list to the current list of 
     * values for the supplied key. If the supplied value list is empty, method 
     * returns immediately. Method throws a {@code NullPointerException} if the 
     * supplied array of values is {@code null}.
     * <p />
     * NOTE: This implementation ignores {@code null} values; Any {@code null} value
     * in the supplied value list is ignored and not added to the value list. Overriding
     * implementations may modify this behavior by redefining the
     * {@link #addNull(java.util.List)} method.
     *
     * @param key the key.
     * @param valueList the list of values to be added.
     * @throws NullPointerException if the supplied value list is {@code null}.
     */
    public final void addAll(K key, List<V> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return;
        }
        
        List<V> values = getValues(key);

        for (V value : valueList) {
            if (value != null) {
                values.add(value);
            } else {
                addNull(values);
            }            
        }        
    }
    
    @Override
    public final V getFirst(K key) {
        List<V> values = get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

    /**
     * Add a value to the first position in the current list of values for the
     * supplied key.
     * <p />
     * NOTE: This implementation ignores {@code null} values; A supplied value
     * of {@code null} is ignored and not added to the purged value list. Overriding
     * implementations may modify this behavior by redefining the
     * {@link #addFirstNull(java.util.List)} method.
     *
     * @param key the key
     * @param value the value to be added.
     */
    public final void addFirst(K key, V value) {
        List<V> values = getValues(key);

        if (value != null) {
            values.add(0, value);
        } else {
            addFirstNull(values);
        }
    }

    /**
     * Return a non-null list of values for a given key. The returned list may be
     * empty.
     * <p />
     * If there is no entry for the key in the map, a new empty {@link List}
     * instance is created, registered within the map to hold the values of
     * the key and returned from the method.
     *
     * @param key the key.
     * @return value list registered with the key. The method is guaranteed to never
     *     return {@code null}.
     */
    protected final List<V> getValues(K key) {
        List<V> l = get(key);
        if (l == null) {
            l = new LinkedList<V>();
            put(key, l);
        }
        return l;
    }
}
