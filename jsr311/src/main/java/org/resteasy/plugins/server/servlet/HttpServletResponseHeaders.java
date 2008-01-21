package org.resteasy.plugins.server.servlet;

import org.resteasy.specimpl.MultivaluedMapImpl;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseHeaders implements MultivaluedMap<String, Object>
{

   private MultivaluedMap<String, Object> cachedHeaders = new MultivaluedMapImpl<String, Object>();
   private HttpServletResponse response;

   public HttpServletResponseHeaders(HttpServletResponse response)
   {
      this.response = response;
   }

   public void putSingle(String key, Object value)
   {
      cachedHeaders.putSingle(key, value);
      response.addHeader(key, value.toString());
   }

   public void add(String key, Object value)
   {
      cachedHeaders.add(key, value);
      response.addHeader(key, value.toString());
   }

   public Object getFirst(String key)
   {
      return cachedHeaders.getFirst(key);
   }

   public int size()
   {
      return cachedHeaders.size();
   }

   public boolean isEmpty()
   {
      return cachedHeaders.isEmpty();
   }

   public boolean containsKey(Object o)
   {
      return cachedHeaders.containsKey(o);
   }

   public boolean containsValue(Object o)
   {
      return cachedHeaders.containsValue(o);
   }

   public List<Object> get(Object o)
   {
      return cachedHeaders.get(o);
   }

   public List<Object> put(String s, List<Object> objs)
   {
      for (Object obj : objs)
      {
         response.addHeader(s, obj.toString());
      }
      return cachedHeaders.put(s, objs);
   }

   public List<Object> remove(Object o)
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public void putAll(Map<? extends String, ? extends List<Object>> map)
   {
      throw new RuntimeException("putAll() on this class not supported yet");
   }

   public void clear()
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public Set<String> keySet()
   {
      return cachedHeaders.keySet();
   }

   public Collection<List<Object>> values()
   {
      return cachedHeaders.values();
   }

   public Set<Entry<String, List<Object>>> entrySet()
   {
      return cachedHeaders.entrySet();
   }

   public boolean equals(Object o)
   {
      return cachedHeaders.equals(o);
   }

   public int hashCode()
   {
      return cachedHeaders.hashCode();
   }
}
