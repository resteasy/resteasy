package org.jboss.resteasy.client.cache;


import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.ReadFromStream;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An HTTP cache that behaves in the same way a browser should behave obeying Cache-Control headers and such.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BrowserCache
{
   public static class Header
   {
      private String name;
      private String value;

      public Header(String name, String value)
      {
         this.name = name;
         this.value = value;
      }

      public String getName()
      {
         return name;
      }

      public String getValue()
      {
         return value;
      }
   }

   public static class CacheEntry
   {
      private final MediaType mediaType;
      private final byte[] cached;
      private final int expires;
      private final long timestamp = System.currentTimeMillis();
      private final MultivaluedMap<String, String> headers;
      private Header[] validationHeaders = {};

      private CacheEntry(MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag, String lastModified, MediaType mediaType)
      {
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

   private Map<String, Map<MediaType, CacheEntry>> cache = new ConcurrentHashMap<String, Map<MediaType, CacheEntry>>();

   public Map<MediaType, CacheEntry> get(String key)
   {
      return cache.get(key);
   }


   public boolean isCacheable(MultivaluedMap<String, String> headers)
   {
      String cc = headers.getFirst(HttpHeaders.CACHE_CONTROL);

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache()) return false;
         return true;
      }
      else
      {
         return headers.containsKey(HttpHeaders.EXPIRES) ||
                 headers.containsKey(HttpHeaders.LAST_MODIFIED) ||
                 headers.containsKey(HttpHeaders.ETAG);
      }
   }

   public ClientResponse cacheIfPossible(ClientRequest request, BaseClientResponse response) throws Exception
   {
      String cc = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getHeaders().getFirst(HttpHeaders.EXPIRES);
      int expires = -1;

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache()) return response;
         expires = cacheControl.getMaxAge();
      }
      else if (exp != null)
      {
         Date date = DateUtil.parseDate(exp);
         expires = (int) ((date.getTime() - System.currentTimeMillis()) / 1000);
      }

      String lastModified = (String) response.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getHeaders().getFirst(HttpHeaders.ETAG);

      String contentType = (String) response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

      byte[] cached = ReadFromStream.readFromStream(1024, response.getInputStream());
      response.releaseConnection();

      CacheEntry entry = put(request.getUri(), MediaType.valueOf(contentType), (MultivaluedMap<String, String>) response.getHeaders(), cached, expires, etag, lastModified);

      return new CachedClientResponse(entry, request.getProviderFactory());
   }

   public ClientResponse updateOnNotModified(ClientRequest request, CacheEntry old, BaseClientResponse response) throws Exception
   {
      old.getHeaders().remove(HttpHeaders.CACHE_CONTROL);
      old.getHeaders().remove(HttpHeaders.EXPIRES);
      old.getHeaders().remove(HttpHeaders.LAST_MODIFIED);
      String cc = (String) response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getHeaders().getFirst(HttpHeaders.EXPIRES);
      int expires = -1;

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache())
         {
            return new CachedClientResponse(old, request.getProviderFactory());
         }
         expires = cacheControl.getMaxAge();
      }
      else if (exp != null)
      {
         Date date = DateUtil.parseDate(exp);
         expires = (int) ((date.getTime() - System.currentTimeMillis()) / 1000);
      }

      if (cc != null)
      {
         old.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cc);
      }
      if (exp != null)
      {
         old.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, exp);
      }

      String lastModified = (String) response.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getHeaders().getFirst(HttpHeaders.ETAG);

      if (etag == null) etag = old.getHeaders().getFirst(HttpHeaders.ETAG);
      else old.getHeaders().putSingle(HttpHeaders.ETAG, etag);

      if (lastModified != null)
      {
         old.getHeaders().putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
      }

      if (etag == null && lastModified == null && cc == null && exp == null) // don't cache
      {
         return new CachedClientResponse(old, request.getProviderFactory());
      }


      CacheEntry entry = put(request.getUri(), old.getMediaType(), old.getHeaders(), old.getCached(), expires, etag, lastModified);
      return new CachedClientResponse(entry, request.getProviderFactory());

   }

   public CacheEntry put(String key, MediaType mediaType, MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag, String lastModified)
   {
      Map<MediaType, CacheEntry> map = cache.get(key);
      if (map == null)
      {
         map = new ConcurrentHashMap<MediaType, CacheEntry>();
         cache.put(key, map);
      }
      CacheEntry cacheEntry = new CacheEntry(headers, cached, expires, etag, lastModified, mediaType);
      map.put(mediaType, cacheEntry);
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
