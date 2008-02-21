package org.resteasy;

import org.resteasy.specimpl.MultivaluedMapImpl;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Headers extends MultivaluedMapImpl<String, String>
{
   public Headers()
   {
   }

   public void add(String key, String value)
   {
      super.add(key.toLowerCase(), value);
   }

   public void putSingle(String key, String value)
   {
      super.putSingle(key.toLowerCase(), value);
   }

   public String getFirst(String key)
   {
      return super.getFirst(key.toLowerCase());
   }

   public boolean containsKey(Object o)
   {
      return super.containsKey(o.toString().toLowerCase());
   }

   public List<String> get(Object o)
   {
      return super.get(o.toString().toLowerCase());
   }

   public List<String> put(String s, List<String> strings)
   {
      return super.put(s.toLowerCase(), strings);
   }

   public void putAll(Map<? extends String, ? extends List<String>> map)
   {
      for (String key : map.keySet())
      {
         put(key, map.get(key));
      }
   }

   public List<String> remove(Object o)
   {
      return super.remove(o.toString().toLowerCase());
   }
}
