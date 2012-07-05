package org.jboss.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * {@link javax.ws.rs.core.MultivaluedMap} implementation that delegates to another instance.  Also keeps a version count
 * that gets incremented any time something is added or removed fro the map.  But, will not bump version if something
 * is added or removed from an individual list entry.
 *
 * @param <K> The type of keys in the map.
 * @param <V> The type of values in the lists in the map.
 */
public class VersionedMultivaluedMap<K, V> implements MultivaluedMap<K, V>
{

   private MultivaluedMap<K, V> delegate;
   private int version;

   public VersionedMultivaluedMap(MultivaluedMap<K, V> delegate)
   {
      this.delegate = delegate;
   }

   public int getVersion()
   {
      return version;
   }


   public MultivaluedMap<K, V> getDelegate()
   {
      return delegate;
   }

   public void setDelegate(MultivaluedMap<K, V> delegate)
   {
      version++;
      this.delegate = delegate;
   }

   @Override
   public void addAll(K key, V... newValues)
   {
      version++;
      this.delegate.addAll(key, newValues);
   }

   @Override
   public void addAll(K key, List<V> valueList)
   {
      version++;
      this.delegate.addAll(key, valueList);
   }

   @Override
   public void addFirst(K key, V value)
   {
      version++;
      this.delegate.addFirst(key, value);
   }

   public void putSingle(K key, V value)
   {
      version++;
      delegate.putSingle(key, value);
   }

   public void add(K key, V value)
   {
      version++;
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
      final List<V> list = delegate.get(o);
      return new List<V>()
      {
         public int size()
         {
            return list.size();
         }

         public boolean isEmpty()
         {
            return list.isEmpty();
         }

         public boolean contains(Object o)
         {
            return list.contains(o);
         }

         public Iterator<V> iterator()
         {
            return list.iterator();
         }

         public Object[] toArray()
         {
            return list.toArray();
         }

         public <T> T[] toArray(T[] ts)
         {
            return list.toArray(ts);
         }

         public boolean add(V v)
         {
            version++;
            return list.add(v);
         }

         public boolean remove(Object o)
         {
            version++;
            return list.remove(o);
         }

         public boolean containsAll(Collection<?> objects)
         {
            return list.containsAll(objects);
         }

         public boolean addAll(Collection<? extends V> vs)
         {
            version++;
            return list.addAll(vs);
         }

         public boolean addAll(int i, Collection<? extends V> vs)
         {
            version++;
            return list.addAll(i, vs);
         }

         public boolean removeAll(Collection<?> objects)
         {
            version++;
            return list.removeAll(objects);
         }

         public boolean retainAll(Collection<?> objects)
         {
            version++;
            return list.retainAll(objects);
         }

         public void clear()
         {
            list.clear();
         }

         @Override
         public boolean equals(Object o)
         {
            return list.equals(o);
         }

         @Override
         public int hashCode()
         {
            return list.hashCode();
         }

         public V get(int i)
         {
            return list.get(i);
         }

         public V set(int i, V v)
         {
            version++;
            return list.set(i, v);
         }

         public void add(int i, V v)
         {
            version++;
            list.add(i, v);
         }

         public V remove(int i)
         {
            version++;
            return list.remove(i);
         }

         public int indexOf(Object o)
         {
            return list.indexOf(o);
         }

         public int lastIndexOf(Object o)
         {
            return list.lastIndexOf(o);
         }

         public ListIterator<V> listIterator()
         {
            return list.listIterator();
         }

         public ListIterator<V> listIterator(int i)
         {
            return list.listIterator(i);
         }

         public List<V> subList(int i, int i1)
         {
            return list.subList(i, i1);
         }
      };
   }

   public List<V> put(K k, List<V> vs)
   {
      version++;
      return delegate.put(k, vs);
   }

   @Override
   public List<V> remove(Object o)
   {
      version++;
      return delegate.remove(o);
   }

   public void putAll(Map<? extends K, ? extends List<V>> map)
   {
      version++;
      delegate.putAll(map);
   }

   @Override
   public void clear()
   {
      version++;
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
}
