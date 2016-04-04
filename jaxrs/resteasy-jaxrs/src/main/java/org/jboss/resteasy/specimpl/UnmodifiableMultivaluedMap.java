package org.jboss.resteasy.specimpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Unmodifiable implementation of {@code MultivaluedMap} interface
 *
 * @author <a href="mailto:mstefank@redhat.conm">Martin Stefanko</a>
 * @version $Revision: 1 $
 */
public class UnmodifiableMultivaluedMap<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V> {

   @SuppressWarnings("unchecked")
   public UnmodifiableMultivaluedMap(final MultivaluedMap<? extends K, ? extends V> map) {
      super((MultivaluedMap<K, V>) map);
   }

   @SuppressWarnings("unchecked")
   public static <K, V> UnmodifiableMultivaluedMap<K, V> unmodifiableMultiValuedMap(
           MultivaluedMap<? extends K, ? extends V> map) {
      return new UnmodifiableMultivaluedMap(map);
   }

   @Override
   public V getFirst(K key) {
      List<V> list = get(key);
      return list == null ? null : list.get(0);
   }

   @Override
   public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> omap) {
      if (this == omap) {
         return true;
      }
      if (!keySet().equals(omap.keySet())) {
         return false;
      }
      for (Map.Entry<K, List<V>> e : entrySet()) {
         List<V> olist = omap.get(e.getKey());
         if (e.getValue().size() != olist.size()) {
            return false;
         }
         for (V v : e.getValue()) {
            if (!olist.contains(v)) {
               return false;
            }
         }
      }
      return true;
   }

   @Override
   public List<V> get(Object key) {
      List<V> value = super.get(key);
      return value == null ? null : Collections.unmodifiableList(value);
   }

   @Override
   public List<V> put(K key, List<V> value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(K k, V v) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addAll(K k, V... vs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addAll(K k, List<V> list) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addFirst(K k, V v) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putSingle(K k, V v) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(Map<? extends K, ? extends List<V>> m) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean remove(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> getOrDefault(Object key, List<V> defaultValue) {
      return Collections.unmodifiableList(super.getOrDefault(key, defaultValue));
   }

   @Override
   public List<V> putIfAbsent(K key, List<V> value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean replace(K key, List<V> oldValue, List<V> newValue) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> replace(K key, List<V> value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> merge(K key, List<V> value, BiFunction<? super List<V>, ? super List<V>, ? extends List<V>> remappingFunction) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void replaceAll(BiFunction<? super K, ? super List<V>, ? extends List<V>> function) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> compute(K key, BiFunction<? super K, ? super List<V>, ? extends List<V>> remappingFunction) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> computeIfAbsent(K key, Function<? super K, ? extends List<V>> mappingFunction) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<V> computeIfPresent(K key, BiFunction<? super K, ? super List<V>, ? extends List<V>> remappingFunction) {
      throw new UnsupportedOperationException();
   }
}
