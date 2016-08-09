package org.jboss.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link javax.ws.rs.core.MultivaluedMap} implementation that delegates to another instance.
 * Convenience class for {@link javax.ws.rs.core.MultivaluedMap} enhancements that don't want to implement all methods.
 *
 * @param <K> The type of keys in the map.
 * @param <V> The type of values in the lists in the map.
 */
public class DelegatingMultivaluedMap<K, V> implements MultivaluedMap<K, V>
{

   private final MultivaluedMap<K, V> delegate;

   public DelegatingMultivaluedMap(MultivaluedMap<K, V> delegate)
   {
      this.delegate = delegate;
   }

   @SuppressWarnings(value = "unchecked")
   @Override
   public void addAll(K key, V... newValues)
   {
      this.delegate.addAll(key, newValues);
   }

   @Override
   public void addAll(K key, List<V> valueList)
   {
      this.delegate.addAll(key, valueList);
   }

   @Override
   public void addFirst(K key, V value)
   {
      this.delegate.addFirst(key, value);
   }

   public void putSingle(K key, V value)
   {
      delegate.putSingle(key, value);
   }

   public void add(K key, V value)
   {
      delegate.add(key, value);
   }

   public V getFirst(K key)
   {
      return delegate.getFirst(key);
   }

   @Override
   public int size()
   {
      return delegate.size();
   }

   @Override
   public boolean isEmpty()
   {
      return delegate.isEmpty();
   }

   @Override
   public boolean containsKey(Object o)
   {
      return delegate.containsKey(o);
   }

   @Override
   public boolean containsValue(Object o)
   {
      return delegate.containsValue(o);
   }

   @Override
   public List<V> get(Object o)
   {
      return delegate.get(o);
   }

   public List<V> put(K k, List<V> vs)
   {
      return delegate.put(k, vs);
   }

   @Override
   public List<V> remove(Object o)
   {
      return delegate.remove(o);
   }

   public void putAll(Map<? extends K, ? extends List<V>> map)
   {
      delegate.putAll(map);
   }

   @Override
   public void clear()
   {
      delegate.clear();
   }

   @Override
   public Set<K> keySet()
   {
      return delegate.keySet();
   }

   @Override
   public Collection<List<V>> values()
   {
      return delegate.values();
   }

   @Override
   public Set<Entry<K, List<V>>> entrySet()
   {
      return delegate.entrySet();
   }

   @Override
   public boolean equals(Object o)
   {
      return delegate.equals(o);
   }

   @Override
   public int hashCode()
   {
      return delegate.hashCode();
   }

   public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> otherMap)
   {
      return delegate.equalsIgnoreValueOrder(otherMap);
   }
}
