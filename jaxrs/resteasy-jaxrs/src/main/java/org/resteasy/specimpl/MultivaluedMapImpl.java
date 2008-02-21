package org.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultivaluedMapImpl<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V>
{

   public void putSingle(K key, V value)
   {
      List<V> list = new ArrayList<V>(1);
      list.add(value);
      put(key, list);
   }

   public void add(K key, V value)
   {
      List<V> list = get(key);
      if (list == null)
      {
         list = new ArrayList<V>(1);
         put(key, list);
      }
      list.add(value);
   }

   public V getFirst(K key)
   {
      List<V> list = get(key);
      return (list == null) ? null : list.get(0);
   }
}
