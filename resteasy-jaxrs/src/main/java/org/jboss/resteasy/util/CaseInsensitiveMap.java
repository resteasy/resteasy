package org.jboss.resteasy.util;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CaseInsensitiveMap<V> implements MultivaluedMap<String, V>, Serializable
{

   private static class KeySetWrapper implements Set<String>
   {
      private Set<CaseInsensitiveKey> keys;

      public KeySetWrapper(Set<CaseInsensitiveKey> keys)
      {
         this.keys = keys;
      }

      public int size()
      {
         return keys.size();
      }

      public boolean isEmpty()
      {
         return keys.isEmpty();
      }

      public boolean contains(Object o)
      {
         return keys.contains(new CaseInsensitiveKey((String) o));
      }

      public Iterator<String> iterator()
      {
         return new Iterator<String>()
         {
            private Iterator<CaseInsensitiveKey> it = keys.iterator();

            public boolean hasNext()
            {
               return it.hasNext();
            }

            public String next()
            {
               return it.next().key;
            }

            public void remove()
            {
               it.remove();
            }
         };
      }

      public Object[] toArray()
      {
         return toArray(new String[keys.size()]);
      }

      public <T> T[] toArray(T[] ks)
      {
         int i = 0;
         for (CaseInsensitiveKey key : keys)
         {
            ks[i++] = (T) key.key;
         }
         return ks;
      }

      public boolean add(String key)
      {
         return keys.add(new CaseInsensitiveKey(key));
      }

      public boolean remove(Object o)
      {
         return keys.remove(new CaseInsensitiveKey(o.toString()));
      }

      public boolean containsAll(Collection<?> objects)
      {
         HashSet<CaseInsensitiveKey> objs = new HashSet<CaseInsensitiveKey>();
         for (Object o : objects)
         {
            objs.add(new CaseInsensitiveKey(o.toString()));
         }
         return keys.containsAll(objs);
      }

      public boolean addAll(Collection<? extends String> objects)
      {
         HashSet<CaseInsensitiveKey> objs = new HashSet<CaseInsensitiveKey>();
         for (Object o : objects)
         {
            objs.add(new CaseInsensitiveKey(o.toString()));
         }
         return keys.addAll(objs);
      }

      public boolean retainAll(Collection<?> objects)
      {
         HashSet<CaseInsensitiveKey> objs = new HashSet<CaseInsensitiveKey>();
         for (Object o : objects)
         {
            objs.add(new CaseInsensitiveKey(o.toString()));
         }
         return keys.retainAll(objects);
      }

      public boolean removeAll(Collection<?> objects)
      {
         HashSet<CaseInsensitiveKey> objs = new HashSet<CaseInsensitiveKey>();
         for (Object o : objects)
         {
            objs.add(new CaseInsensitiveKey(o.toString()));
         }
         return keys.removeAll(objects);
      }

      public void clear()
      {
         keys.clear();
      }

      public boolean equals(Object o)
      {
         return keys.equals(o);
      }

      public int hashCode()
      {
         return keys.hashCode();
      }
   }

   private static class EntrySetWrapper<V> implements Set<Entry<String, V>>
   {
      private Set<Entry<CaseInsensitiveKey, V>> entrySet;

      private EntrySetWrapper(Set<Entry<CaseInsensitiveKey, V>> entrySet)
      {
         this.entrySet = entrySet;
      }

      public int size()
      {
         return entrySet.size();
      }

      public boolean isEmpty()
      {
         return entrySet.isEmpty();
      }

      public boolean contains(Object o)
      {
         if (!(o instanceof Entry)) return false;
         final Entry<String, V> entry = (Entry<String, V>) o;

         Entry<CaseInsensitiveKey, V> wrapped = new EntryWrapper<V>(entry);

         return entrySet.contains(wrapped);
      }

      public Iterator<Entry<String, V>> iterator()
      {
         return new Iterator<Entry<String, V>>()
         {
            Iterator<Entry<CaseInsensitiveKey, V>> it = entrySet.iterator();

            public boolean hasNext()
            {
               return it.hasNext();
            }

            public Entry<String, V> next()
            {
               return new EntryDelegate<V>(it.next());
            }

            public void remove()
            {
               it.remove();
            }
         };
      }

      public Object[] toArray()
      {
         Entry<String, V>[] array = new Entry[entrySet.size()];
         return toArray(array);
      }

      public <T> T[] toArray(T[] ts)
      {
         Entry<String, V>[] array = (Entry<String, V>[]) ts;
         int i = 0;
         for (Entry<CaseInsensitiveKey, V> entry : entrySet)
         {
            array[i++] = new EntryDelegate(entry);
         }
         return (T[]) array;
      }

      public boolean add(Entry<String, V> stringVEntry)
      {
         entrySet.add(new EntryWrapper<V>(stringVEntry));
         return false;
      }

      public boolean remove(Object o)
      {
         return entrySet.remove(new EntryWrapper<V>((Entry<String, V>) o));
      }

      public boolean containsAll(Collection<?> objects)
      {
         Collection<Entry<String, V>> list = (Collection<Entry<String, V>>) objects;
         HashSet<Entry<CaseInsensitiveKey, V>> set = new HashSet<Entry<CaseInsensitiveKey, V>>();
         for (Entry<String, V> entry : list)
         {
            set.add(new EntryWrapper<V>(entry));
         }
         return entrySet.containsAll(set);
      }

      public boolean addAll(Collection<? extends Entry<String, V>> entries)
      {
         HashSet<Entry<CaseInsensitiveKey, V>> set = new HashSet<Entry<CaseInsensitiveKey, V>>();
         for (Entry<String, V> entry : entries)
         {
            set.add(new EntryWrapper<V>(entry));
         }
         return entrySet.addAll(set);
      }

      public boolean retainAll(Collection<?> objects)
      {
         Collection<Entry<String, V>> list = (Collection<Entry<String, V>>) objects;
         HashSet<Entry<CaseInsensitiveKey, V>> set = new HashSet<Entry<CaseInsensitiveKey, V>>();
         for (Entry<String, V> entry : list)
         {
            set.add(new EntryWrapper<V>(entry));
         }
         return entrySet.retainAll(set);
      }

      public boolean removeAll(Collection<?> objects)
      {
         Collection<Entry<String, V>> list = (Collection<Entry<String, V>>) objects;
         HashSet<Entry<CaseInsensitiveKey, V>> set = new HashSet<Entry<CaseInsensitiveKey, V>>();
         for (Entry<String, V> entry : list)
         {
            set.add(new EntryWrapper<V>(entry));
         }
         return entrySet.removeAll(set);
      }

      public void clear()
      {
         entrySet.clear();
      }

      private class EntryWrapper<T> implements Entry<CaseInsensitiveKey, T>
      {
         private CaseInsensitiveKey key;
         private T value;

         public EntryWrapper(Entry<String, T> entry)
         {
            key = new CaseInsensitiveKey(entry.getKey());
            value = entry.getValue();
         }

         public CaseInsensitiveKey getKey()
         {
            return key;
         }

         public T getValue()
         {
            return value;
         }

         public T setValue(T v)
         {
            T tmp = value;
            value = v;
            return tmp;
         }
      }

      private class EntryDelegate<T> implements Entry<String, T>
      {
         private Entry<CaseInsensitiveKey, T> entry;

         private EntryDelegate(Entry<CaseInsensitiveKey, T> entry)
         {
            this.entry = entry;
         }

         public String getKey()
         {
            return entry.getKey().key;
         }

         public T getValue()
         {
            return entry.getValue();
         }

         public T setValue(T v)
         {
            return entry.setValue(v);
         }
      }
   }

   private static class CaseInsensitiveKey implements Serializable
   {
      private String key;

      private CaseInsensitiveKey(String key)
      {
         this.key = key;
      }

      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         CaseInsensitiveKey that = (CaseInsensitiveKey) o;

         if (!key.equalsIgnoreCase(that.key)) return false;

         return true;
      }

      public int hashCode()
      {
         return key.toLowerCase().hashCode();
      }

      public String toString()
      {
         return key;
      }
   }

   private MultivaluedMapImpl<CaseInsensitiveKey, V> map = new MultivaluedMapImpl<CaseInsensitiveKey, V>();

   public void putSingle(String key, V value)
   {
      map.putSingle(new CaseInsensitiveKey(key), value);
   }

   public void add(String key, V value)
   {
      map.add(new CaseInsensitiveKey(key), value);
   }

   public V getFirst(String key)
   {
      return map.getFirst(new CaseInsensitiveKey(key));
   }

   public int size()
   {
      return map.size();
   }

   public boolean isEmpty()
   {
      return map.isEmpty();
   }

   public boolean containsKey(Object o)
   {
      return map.containsKey(new CaseInsensitiveKey(o.toString()));
   }

   public boolean containsValue(Object o)
   {
      return map.containsValue(o);
   }

   public List<V> get(Object o)
   {
      return map.get(new CaseInsensitiveKey(o.toString()));
   }

   public List<V> put(String s, List<V> vs)
   {
      return map.put(new CaseInsensitiveKey(s), vs);
   }

   public List<V> remove(Object o)
   {
      return map.remove(new CaseInsensitiveKey(o.toString()));
   }

   public void putAll(Map<? extends String, ? extends List<V>> map)
   {
      for (Map.Entry<? extends String, ? extends List<V>> entry : map.entrySet())
      {
         List<V> objs = entry.getValue();
         for (V obj : objs)
         {
            add(entry.getKey(), obj);
         }
      }
   }

   public void clear()
   {
      map.clear();
   }

   public Set<String> keySet()
   {
      return new KeySetWrapper(map.keySet());
   }

   public Collection<List<V>> values()
   {
      return map.values();
   }

   public Set<Entry<String, List<V>>> entrySet()
   {
      return new EntrySetWrapper<List<V>>(map.entrySet());
   }
}
