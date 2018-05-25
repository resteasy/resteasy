package org.jboss.resteasy.plugins.cache.server;


import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See org.jboss.resteasy.plugins.cache.server.InfinispanCache.
 * @see org.jboss.resteasy.plugins.cache.server.InfinispanCache
 */
@Deprecated
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
         return System.currentTimeMillis() - timestamp >= expires * 1000L;
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


   private Map<String, Map<MediaType, CacheEntry>> cache = new ConcurrentHashMap<String, Map<MediaType, CacheEntry>>();

   public Entry get(String uri, MediaType accept)
   {
      Map<MediaType, CacheEntry> entry = cache.get(uri);
      if (entry == null || entry.isEmpty()) return null;
      for (Map.Entry<MediaType, CacheEntry> produce : entry.entrySet())
      {
         if (accept.isCompatible(produce.getKey())) return produce.getValue();
      }
      return null;
   }

   public Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag)
   {
      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag);
      Map<MediaType, CacheEntry> entry = cache.get(uri);
      if (entry == null)
      {
         entry = new ConcurrentHashMap<MediaType, CacheEntry>();
         cache.put(uri, entry);
      }
      entry.put(mediaType, cacheEntry);
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
