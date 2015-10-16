package org.jboss.resteasy.client.jaxrs.cache;

import org.jboss.resteasy.client.jaxrs.cache.BrowserCache.Entry;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCache.Header;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheEntry implements Entry, Serializable
{
   private static final long serialVersionUID = -1922521972113619372L;
   private final String mediaType;
   private final byte[] cached;
   private final int expires;
   private final long timestamp = System.currentTimeMillis();
   private final MultivaluedMap<String, String> headers;
   private Header[] validationHeaders = {};
   private final String key;
   private Map extendedProperties = new ConcurrentHashMap();

   public CacheEntry(String key, MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag, String lastModified, MediaType mediaType)
   {
      this.key = key;
      this.cached = cached;
      this.expires = expires;
      this.mediaType = mediaType.toString();
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
      return System.currentTimeMillis() - timestamp >= expires * 1000L;
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
      return MediaType.valueOf(mediaType);
   }

   public void addExtendedProperty(Serializable key, Serializable value)
   {
      extendedProperties.put(key, value);
   }

   public Object getExtendedProperty(Serializable key)
   {
      return extendedProperties.get(key);
   }
}
