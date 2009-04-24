package org.jboss.resteasy.client.core.executors;

import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class URLConnectionHeaderWrapper implements MultivaluedMap<String, String>
{

   private CaseInsensitiveMap<String> cachedHeaders = new CaseInsensitiveMap<String>();
   private URLConnection connection;
   private ResteasyProviderFactory factory;

   public URLConnectionHeaderWrapper(URLConnection connection, ResteasyProviderFactory factory)
   {
      this.connection = connection;
      this.factory = factory;
   }

   public void sync()
   {
      for (Entry<String, List<String>> entry : connection.getRequestProperties().entrySet())
      {
         for (String string : entry.getValue())
         {
            cachedHeaders.add(entry.getKey(), string);
         }
      }
   }

   public void putSingle(String key, String value)
   {
      cachedHeaders.putSingle(key, value);
      addResponseHeader(key, value);
   }

   public void add(String key, String value)
   {
      cachedHeaders.add(key, value);
      addResponseHeader(key, value);
   }

   protected void addResponseHeader(String key, Object value)
   {
      RuntimeDelegate.HeaderDelegate delegate = factory.createHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
         connection.addRequestProperty(key.toLowerCase(), delegate.toString(value));
      }
      else
      {
         //System.out.println("addResponseHeader: " + key + " " + value.toString());
         connection.addRequestProperty(key.toLowerCase(), value.toString());
      }
   }

   public String getFirst(String key)
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

   public List<String> get(Object o)
   {
      return cachedHeaders.get(o);
   }

   public List<String> put(String s, List<String> objs)
   {
      for (String obj : objs)
      {
         addResponseHeader(s, obj);
      }
      return cachedHeaders.put(s, objs);
   }

   public List<String> remove(Object o)
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public void putAll(Map<? extends String, ? extends List<String>> map)
   {
      for (String key : map.keySet())
      {
         for (String obj : map.get(key))
         {
            add(key, obj);
         }
      }
   }

   public void clear()
   {
      throw new RuntimeException("Removing a header is illegal for an HttpServletResponse");
   }

   public Set<String> keySet()
   {
      return cachedHeaders.keySet();
   }

   public Collection<List<String>> values()
   {
      return cachedHeaders.values();
   }

   public Set<Entry<String, List<String>>> entrySet()
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