package org.jboss.resteasy.plugins.cache.server;


import org.infinispan.Cache;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InfinispanCache implements ServerCache
{

   public static class CacheEntry implements Entry
   {
      private final byte[] cached;
      private final int expires;
      private final long timestamp = System.currentTimeMillis();
      private final MultivaluedMap<String, Object> headers;
      private final String etag;
      private final MediaType mediaType;

      private CacheEntry(MultivaluedMap<String, Object> headers, byte[] cached, int expires, String etag, MediaType mediaType)
      {
         this.cached = cached;
         this.expires = expires;
         this.headers = headers;
         this.etag = etag;
         this.mediaType = mediaType;
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

      public MediaType getMediaType()
      {
         return mediaType;
      }
   }

   protected Cache cache;

   public InfinispanCache(Cache cache)
   {
      this.cache = cache;
   }

   public Entry get(String uri, MediaType accept)
   {
      Set<String> entries = (Set<String>)cache.get(uri);
      if (entries == null) return null;

      for (String entry : entries)
      {
         CacheEntry cacheEntry = (CacheEntry)cache.get(entry);
         if (cacheEntry == null) continue;
         if (accept.isCompatible(cacheEntry.getMediaType()))
         {
            return cacheEntry;
         }
      }
      return null;
   }

   public Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag)
   {
      // there's a race condition here with a concurrent get() method above.  Too bad JBoss Cache doesn't have a way to create
      // a node before hand then insert it
      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag, mediaType);
      String entryName = uri + "    " + mediaType.toString();
      Set<String> entries = (Set<String>)cache.get(uri);
      Set<String> newEntries = new HashSet<String>();
      newEntries.add(entryName);
      if (entries != null)
      {
         newEntries.addAll(entries);
      }
      cache.put(uri, newEntries);
      cache.put(entryName, cacheEntry, cc.getMaxAge(), TimeUnit.SECONDS);
      return cacheEntry;
   }

   public void remove(String uri)
   {
      Set<String> entries = (Set<String>)cache.remove(uri);
      if (entries == null) return;
      for (String entry : entries)
      {
         cache.remove(entry);
      }
   }

   public void clear()
   {
      cache.clear();
   }

}