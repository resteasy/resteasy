package org.resteasy;

import org.resteasy.specimpl.MultivaluedMapImpl;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Headers<V> extends MultivaluedMapImpl<String, V>
{
   public Headers()
   {
   }

   public void add(String key, V value)
   {
      super.add(key.toLowerCase(), value);
   }

   public void putSingle(String key, V value)
   {
      super.putSingle(key.toLowerCase(), value);
   }

   public V getFirst(String key)
   {
      return super.getFirst(key.toLowerCase());
   }

   public boolean containsKey(Object o)
   {
      return super.containsKey(o.toString().toLowerCase());
   }

   public List<V> get(Object o)
   {
      return super.get(o.toString().toLowerCase());
   }

   public List<V> put(String s, List<V> strings)
   {
      return super.put(s.toLowerCase(), strings);
   }

   public void putAll(Map<? extends String, ? extends List<V>> map)
   {
      for (String key : map.keySet())
      {
         put(key, map.get(key));
      }
   }

   public List<V> remove(Object o)
   {
      return super.remove(o.toString().toLowerCase());
   }
}
