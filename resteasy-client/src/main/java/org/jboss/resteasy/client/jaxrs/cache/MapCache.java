package org.jboss.resteasy.client.jaxrs.cache;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache implements BrowserCache
{
   protected Map<String, Map<String, Entry>> cache = null;

   public MapCache()
   {
      this(new ConcurrentHashMap<String, Map<String, Entry>>());
   }

   public MapCache(Map<String, Map<String, Entry>> cache)
   {
      this.cache = cache;
   }

   protected Map<String, Map<String, Entry>> createCache()
   {
      return new ConcurrentHashMap<String, Map<String, Entry>>();
   }

   public Entry get(String key, MediaType accept)
   {
      Map<String, Entry> parent = cache.get(key);
      if (parent == null || parent.isEmpty()) {
         return null;
      }
      if (accept.isWildcardType()) {
         // if the client accepts */*, return just the first entry for requested URL
         return parent.entrySet().iterator().next().getValue();
      } else if (accept.isWildcardSubtype()) {
         // if the client accepts <media>/*, return the first entry which media type starts with <media>/
         for (Map.Entry<String, Entry> parentEntry : parent.entrySet()) {
            if (parentEntry.getKey().startsWith(accept.getType() + "/")) {
               return parentEntry.getValue();
            }
         }
      }
      return parent.get(accept.toString());
   }

   public Entry getAny(String key)
   {
      Map<String, Entry> parent = cache.get(key);
      if (parent == null) return null;
      Iterator<Entry> iterator = parent.values().iterator();
      if (iterator.hasNext()) return iterator.next();
      return null;
   }

   public Entry getEntry(String key, MediaType accept)
   {
      Map<String, Entry> parent = cache.get(key);
      if (parent == null) return null;
      return parent.get(accept.toString());
   }

   public Entry remove(String key, MediaType type)
   {
      Map<String, Entry> data = cache.get(key);
      if (data == null) return null;
      Entry removed = data.remove(type.toString());
      if (data.isEmpty())
      {
         cache.remove(key);
      }
      else
      {
         cache.put(key, data);
      }
      return removed;
   }

   public void clear()
   {
      cache.clear();
   }

   public Entry put(CacheEntry cacheEntry)
   {
      Map<String, Entry> map = cache.get(cacheEntry.getKey());
      if (map == null)
      {
         map = new ConcurrentHashMap<String, Entry>();
      }
      map.put(cacheEntry.getMediaType().toString(), cacheEntry);
      cache.put(cacheEntry.getKey(), map);
      return cacheEntry;
   }

   public Entry put(String key, MediaType mediaType,
                    MultivaluedMap<String, String> headers, byte[] cached, int expires,
                    String etag, String lastModified)
   {
      return put(new CacheEntry(key, headers, cached, expires, etag, lastModified, mediaType));
   }
}

