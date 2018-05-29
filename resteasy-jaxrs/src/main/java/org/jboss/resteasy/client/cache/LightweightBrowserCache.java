package org.jboss.resteasy.client.cache;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory BrowserCache. Uses an underlying cache, with ConcurrentMapCache as
 * the default. You specify maximum bytes you want the cache to have. The
 * default is 2Megabytes. If the cache exceeds this amount, it is wiped clean.
 * This rather draconian approach to cache reaping is to avoid synchronization
 * that you would normally have to do in a sophisticated cache.
 * <p>
 * With high concurrent access, because this is not a sophisticated cache,
 * sometimes a cache entry may be lost. It is consistent though.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated Caching in the Resteasy client framework in resteasy-jaxrs is replaced by 
 * caching in the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 * @see org.jboss.resteasy.client.jaxrs.cache.LightweightBrowserCache
 */
@Deprecated
public class LightweightBrowserCache implements BrowserCache
{

   protected long maxBytes = 2000000; // 2 meg default
   protected BrowserCache internalCache = null;
   protected AtomicLong bytes = new AtomicLong(0);

   public LightweightBrowserCache()
   {
      this(new MapCache());
   }

   public LightweightBrowserCache(BrowserCache cache)
   {
      this.internalCache = cache;
   }

   public BrowserCache getInternalCache()
   {
      return internalCache;
   }

   public void setInternalCache(BrowserCache internalCache)
   {
      this.internalCache = internalCache;
   }

   public long getMaxBytes()
   {
      return maxBytes;
   }

   public void setMaxBytes(long maxBytes)
   {
      this.maxBytes = maxBytes;
   }

   public Entry getAny(String key)
   {
      return internalCache.getAny(key);
   }

   public Entry remove(String key, MediaType type)
   {
      Entry entry = internalCache.remove(key, type);
      if (entry != null) bytes.addAndGet(-1 * entry.getCached().length);
      return entry;
   }

   public long getByteCount()
   {
      return bytes.get();
   }

   public Entry get(String key, MediaType accept)
   {
      return internalCache.get(key, accept);
   }

   public Entry put(String key, MediaType mediaType,
                    MultivaluedMap<String, String> headers, byte[] cached, int expires,
                    String etag, String lastModified)
   {
      Entry previousValue = internalCache.get(key, mediaType);

      int sizeDiff = -1;
      if (previousValue == null)
         sizeDiff = cached.length;
      else
         sizeDiff = cached.length - previousValue.getCached().length;

      if (bytes.addAndGet(sizeDiff) > maxBytes)
      {
         clear();
         bytes.addAndGet(sizeDiff);
      }
      return internalCache.put(key, mediaType, headers, cached, expires, etag, lastModified);
   }

   public void clear()
   {
      internalCache.clear();
      bytes.set(0);
   }
}
