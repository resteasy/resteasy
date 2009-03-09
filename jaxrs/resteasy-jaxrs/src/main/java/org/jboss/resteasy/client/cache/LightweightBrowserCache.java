package org.jboss.resteasy.client.cache;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory BrowserCache.  Uses java.util.ConcurrentHashMaps.  You specify maximum bytes you want the cache
 * to have.  The default is 2Megabytes.  If the cache exceeds this amount, it is wiped clean.  This rather draconian
 * approach to cache reaping is to avoid synchronization that you would normally have to do in a sophisticated cache.
 * <p/>
 * With high concurrent access, because this is not a sophisticated cache, sometimes a cache entry may be lost.
 * It is consistent though.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LightweightBrowserCache implements BrowserCache
{


   public static class CacheEntry implements Entry
   {
      private final MediaType mediaType;
      private final byte[] cached;
      private final int expires;
      private final long timestamp = System.currentTimeMillis();
      private final MultivaluedMap<String, String> headers;
      private Header[] validationHeaders = {};
      private final String key;

      public CacheEntry(String key, MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag, String lastModified, MediaType mediaType)
      {
         this.key = key;
         this.cached = cached;
         this.expires = expires;
         this.mediaType = mediaType;
         this.headers = headers;

         if (etag != null || lastModified != null)
         {
            if (etag != null && lastModified != null)
            {
               validationHeaders = new Header[2];
               validationHeaders[0] = new Header("If-Modified-Since", lastModified);
               validationHeaders[1] = new Header("If-None-Match", etag);
            }
            else if (etag != null)
            {
               validationHeaders = new Header[1];
               validationHeaders[0] = new Header("If-None-Match", etag);
            }
            else if (lastModified != null)
            {
               validationHeaders = new Header[1];
               validationHeaders[0] = new Header("If-Modified-Since", lastModified);
            }

         }
      }

      public String getKey()
      {
         return key;
      }

      public MultivaluedMap<String, String> getHeaders()
      {
         return headers;
      }

      public boolean expired()
      {
         return System.currentTimeMillis() - timestamp >= expires * 1000;
      }

      public Header[] getValidationHeaders()
      {
         return validationHeaders;
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

   // we have a wrapper for these two so that there is only one guaranteed volatile memory size we're working with
   private static class CacheHolder
   {
      protected AtomicLong bytes = new AtomicLong(0);
      protected ConcurrentHashMap<String, Map<MediaType, Entry>> cache = new ConcurrentHashMap<String, Map<MediaType, Entry>>();

   }

   protected long maxBytes = 2000000; // 2 meg default
   protected volatile CacheHolder holder = new CacheHolder();

   public long getMaxBytes()
   {
      return maxBytes;
   }

   public void setMaxBytes(long maxBytes)
   {
      this.maxBytes = maxBytes;
   }

   public long getByteCount()
   {
      return holder.bytes.get();
   }

   public Entry getAny(String key)
   {
      Map<MediaType, Entry> parent = holder.cache.get(key);
      if (parent == null) return null;
      Iterator<Entry> iterator = parent.values().iterator();
      if (iterator.hasNext()) return iterator.next();
      return null;
   }

   public void remove(String key, MediaType type)
   {
      CacheHolder tmpHolder = holder;
      Map<MediaType, Entry> parent = tmpHolder.cache.get(key);
      if (parent == null) return;
      Entry entry = parent.remove(type);
      if (entry != null) tmpHolder.bytes.addAndGet(-1 * entry.getCached().length);
   }

   public Entry get(String key, MediaType accept)
   {
      Map<MediaType, Entry> parent = holder.cache.get(key);
      if (parent == null) return null;
      return parent.get(accept);
   }


   public Entry put(String key, MediaType mediaType, MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag, String lastModified)
   {
      CacheEntry cacheEntry = new CacheEntry(key, headers, cached, expires, etag, lastModified, mediaType);

      CacheHolder tmpHolder = holder;
      if (tmpHolder.bytes.addAndGet(cached.length) > maxBytes)
      {
         tmpHolder = new CacheHolder(); // just freakin wipe it clean!!
         tmpHolder.bytes.addAndGet(cached.length);
         holder = tmpHolder;
      }

      Map<MediaType, Entry> map = tmpHolder.cache.get(key);
      if (map == null)
      {
         map = new ConcurrentHashMap<MediaType, Entry>();
         Map<MediaType, Entry> tmp = tmpHolder.cache.putIfAbsent(key, map);
         map = (tmp == null) ? map : tmp;
      }
      map.put(mediaType, cacheEntry);
      return cacheEntry;
   }

   public void clear()
   {
      holder.cache.clear();
   }

}
