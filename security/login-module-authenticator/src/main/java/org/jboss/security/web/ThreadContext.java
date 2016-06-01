package org.jboss.security.web;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal wrapper so we can tunnel information between the transport protocol layer and the security domain
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ThreadContext
{
   private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

   public static Object get(String key)
   {
      Map<String, Object> map = context.get();
      if (map == null) return null;
      return map.get(key);
   }

   public static void set(String key, Object value)
   {
      Map<String, Object> map = context.get();
      if (map == null)
      {
         map = new HashMap<String, Object>();
         context.set(map);
      }
      map.put(key, value);
   }

   public static void clear()
   {
      context.set(null);
   }


}
