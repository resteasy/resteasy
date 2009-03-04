package org.jboss.resteasy.plugins.interceptors.cache;


import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleServerCache implements ServerCache
{
   public static class CacheEntry implements Entry
   {
      private final byte[] cached;
      private final int expires;
      private final long timestamp = System.currentTimeMillis();
      private final MultivaluedMap<String, Object> headers;
      private String etag;

      private CacheEntry(MultivaluedMap<String, Object> headers, byte[] cached, int expires, String etag)
      {
         this.cached = cached;
         this.expires = expires;
         this.headers = headers;
         this.etag = etag;
      }

      public int getExpirationInSeconds()
      {
         return expires - (int) ((System.currentTimeMillis() - timestamp) / 1000);
      }

      public boolean isExpired()
      {
         return System.currentTimeMillis() - timestamp >= expires * 1000;
      }

      public String getEtag()
      {
         return etag;
      }

      public MultivaluedMap<String, Object> getHeaders()
      {
         return headers;
      }

      public byte[] getCached()
      {
         return cached;
      }

   }


   private Map<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>();

   public Entry get(String uri)
   {
      return cache.get(uri);
   }

   public Entry add(String uri, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag)
   {
      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag);
      cache.put(uri, cacheEntry);
      return cacheEntry;
   }

   public void remove(String key)
   {
      cache.remove(key);
   }

   public void clear()
   {
      cache.clear();
   }

}