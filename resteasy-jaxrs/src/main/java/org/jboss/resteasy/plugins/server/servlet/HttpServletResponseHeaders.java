package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings(value = "unchecked")
public class HttpServletResponseHeaders implements MultivaluedMap<String, Object>
{

   private CaseInsensitiveMap cachedHeaders = new CaseInsensitiveMap();
   private HttpServletResponse response;
   private ResteasyProviderFactory factory;

   public HttpServletResponseHeaders(HttpServletResponse response, ResteasyProviderFactory factory)
   {
      this.response = response;
      this.factory = factory;
   }

   @Override
   public void addAll(String key, Object... newValues)
   {
      for (Object value : newValues)
      {
         add(key, value);
      }
   }

   @Override
   public void addAll(String key, List<Object> valueList)
   {
      for (Object value : valueList)
      {
         add(key, value);
      }
   }

   @Override
   public void addFirst(String key, Object value)
   {
      List<Object> list = get(key);
      if (list == null)
      {
         add(key, value);
         return;
      }
      else
      {
         list.add(0, value);
      }
   }

   public void putSingle(String key, Object value)
   {
      if (value == null)
      {
         return;
      }
      cachedHeaders.putSingle(key, value);
      RuntimeDelegate.HeaderDelegate delegate = factory.getHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
         response.setHeader(key, delegate.toString(value));
      }
      else
      {
         //System.out.println("addResponseHeader: " + key + " " + value.toString());
         response.setHeader(key, value.toString());
      }
   }

   public void add(String key, Object value)
   {
      cachedHeaders.add(key, value);
      addResponseHeader(key, value);
   }

   protected void addResponseHeader(String key, Object value)
   {
      if (value == null)
      {
         return;
      }
      RuntimeDelegate.HeaderDelegate delegate = factory.getHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
         response.addHeader(key, delegate.toString(value));
      }
      else
      {
         //System.out.println("addResponseHeader: " + key + " " + value.toString());
         response.addHeader(key, value.toString());
      }
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
         addResponseHeader(s, obj);
      }
      return cachedHeaders.put(s, objs);
   }

   public List<Object> remove(Object o)
   {
      throw new RuntimeException(Messages.MESSAGES.removingHeaderIllegal());
   }

   public void putAll(Map<? extends String, ? extends List<Object>> map)
   {
      for (Map.Entry<? extends String, ? extends List<Object>> entry : map.entrySet())
      {
         List<Object> objs = entry.getValue();
         // When the header key does not exist, undertow creates it and saves this value.
         // When the header key exists undertow clears the existing values and saves this value.
         // All subsequent values must be added so they will be appended properly.
         if (!objs.isEmpty()) {
            putResponseHeader(entry.getKey(), objs.get(0));
            for (int i = 1; i < objs.size(); i++) {
               add(entry.getKey(), objs.get(i));
            }
         }
      }
   }

   protected void putResponseHeader(String key, Object value)
   {
      if (value == null)
      {
         return;
      }
      RuntimeDelegate.HeaderDelegate delegate = factory.getHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("putResponseHeader: " + key + " " + delegate.toString(value));
         response.setHeader(key, delegate.toString(value));
      }
      else
      {
         //System.out.println("putResponseHeader: " + key + " " + value.toString());
         response.setHeader(key, value.toString());
      }
      cachedHeaders.add(key, value);
   }

   public void clear()
   {
      throw new RuntimeException(Messages.MESSAGES.removingHeaderIllegal());
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

   @Override
   public boolean equalsIgnoreValueOrder(MultivaluedMap<String, Object> otherMap)
   {
      return cachedHeaders.equalsIgnoreValueOrder(otherMap);
   }
}
