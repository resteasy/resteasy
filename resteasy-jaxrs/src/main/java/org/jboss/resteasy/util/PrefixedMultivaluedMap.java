package org.jboss.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link javax.ws.rs.core.MultivaluedMap} implementation that wraps another instance and only returns values that are prefixed with the given {@link #prefixWithDot}.
 * 
 * @param <V> The type of the values in the lists in the map.
 */
public class PrefixedMultivaluedMap<V> extends DelegatingMultivaluedMap<String, V>
{

   private final String prefixWithDot;

   /**
    * Constructor setting the prefix and the delegate.
    * @param prefix prefix
    * @param delegate delegate map
    */
   public PrefixedMultivaluedMap(String prefix, MultivaluedMap<String, V> delegate)
   {
      super(delegate);
      this.prefixWithDot = prefix + ".";
   }

   /**
    * Returns the value assigned to "<i>prefix</i>.<i>key</i>" implicitly converts the key to {@link String}.
    * @param key key
    * @return values
    */
   @Override
   public List<V> get(Object key)
   {
      return super.get(prefixWithDot + key);
   }

   @Override
   public Set<String> keySet()
   {
      HashSet<String> result = new HashSet<String>();
      for (String key : super.keySet())
      {
         if (key.startsWith(prefixWithDot))
         {
            result.add(key.substring(prefixWithDot.length()));
         }
      }
      return result;
   }


}
