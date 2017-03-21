package org.jboss.resteasy.client.jaxrs.cache;

import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.ReadFromStream;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class CacheInterceptor implements ClientRequestFilter, ClientResponseFilter
{
   protected BrowserCache cache;

   public CacheInterceptor(BrowserCache cache)
   {
      LogMessages.LOGGER.debugf("Interceptor : %s,  Method : CacheInterceptor", getClass().getName());
      this.cache = cache;
   }

   @Override
   public void filter(ClientRequestContext request) throws IOException
   {
      if (!request.getMethod().equalsIgnoreCase("GET")) return;
      try
      {
         BrowserCache.Entry entry = getEntry(request);
         if (entry == null) return;
         if (entry.expired())
         {
            // entry should have a remove method
            cache.remove(request.getUri().toString(), entry.getMediaType());

            // add validation headers
            BrowserCache.Header[] headers = entry.getValidationHeaders();
            for (BrowserCache.Header header : headers)
            {
               request.getHeaders().putSingle(header.getName(), header.getValue());
            }
            request.setProperty("expired.cache.entry", entry);
            return;
         }
         request.setProperty("cached", "cached");
         request.abortWith(cachedResponse(entry));
      }
      catch (IOException io)
      {
         throw io;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private Response cachedResponse(BrowserCache.Entry entry)
   {
      ByteArrayInputStream bais = new ByteArrayInputStream(entry.getCached());
      Response.ResponseBuilder builder = Response.ok().entity(bais);
      for (Map.Entry<String, List<String>> header : entry.getHeaders().entrySet())
      {
         for (String val : header.getValue())
         {
            builder.header(header.getKey(), val);
         }
      }
      return builder.build();
   }

   @Override
   public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException
   {
      if (!request.getMethod().equalsIgnoreCase("GET") || request.getProperty("cached") != null) return;
      else if (response.getStatus() == 304)
      {
         BrowserCache.Entry entry = (BrowserCache.Entry)request.getProperty("expired.cache.entry");
         updateOnNotModified(request, entry, response);
         return;
      }
      else if (response.getStatus() == 200)
      {
         cache(request, response);
      }
   }

   private void useCacheEntry(ClientResponseContext response, BrowserCache.Entry entry)
   {
      ByteArrayInputStream bais = new ByteArrayInputStream(entry.getCached());
      response.setEntityStream(bais);
      response.setStatus(200);

      for (Map.Entry<String, List<String>> header : entry.getHeaders().entrySet())
      {
         response.getHeaders().remove(header.getKey());
         for (String val : header.getValue())
         {
            response.getHeaders().add(header.getKey(), val);
         }
      }
   }

   private void cache(ClientRequestContext request, ClientResponseContext response) throws IOException
   {
      if (response.getStatus() != 200) return;
      cacheIfPossible(request, response);
   }

   public void updateOnNotModified(ClientRequestContext request, BrowserCache.Entry old, ClientResponseContext response)
   {
      old.getHeaders().remove(HttpHeaders.CACHE_CONTROL);
      old.getHeaders().remove(HttpHeaders.EXPIRES);
      old.getHeaders().remove(HttpHeaders.LAST_MODIFIED);
      String cc = (String) response.getHeaderString(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getHeaderString(HttpHeaders.EXPIRES);
      int expires = -1;

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache())
         {
            useCacheEntry(response, old);
            return;
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

      String lastModified = (String) response.getHeaderString(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getHeaderString(HttpHeaders.ETAG);

      if (etag == null) etag = old.getHeaders().getFirst(HttpHeaders.ETAG);
      else old.getHeaders().putSingle(HttpHeaders.ETAG, etag);

      if (lastModified != null)
      {
         old.getHeaders().putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
      }

      if (etag == null && lastModified == null && cc == null && exp == null) // don't cache
      {
         useCacheEntry(response, old);
         return;
      }


      BrowserCache.Entry entry = cache.put(request.getUri().toString(), old.getMediaType(), old.getHeaders(), old.getCached(), expires, etag, lastModified);
      useCacheEntry(response, entry);

   }



   public void cacheIfPossible(ClientRequestContext request, ClientResponseContext response) throws IOException
   {
      String cc = (String) response.getHeaderString(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getHeaderString(HttpHeaders.EXPIRES);
      int expires = -1;

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache()) return;
         expires = cacheControl.getMaxAge();
      }
      else if (exp != null)
      {
         Date date = DateUtil.parseDate(exp);
         expires = (int) ((date.getTime() - System.currentTimeMillis()) / 1000);
      }

      String lastModified = (String) response.getHeaderString(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getHeaderString(HttpHeaders.ETAG);

      String contentType = (String) response.getHeaderString(HttpHeaders.CONTENT_TYPE);

      byte[] cached = ReadFromStream.readFromStream(1024, response.getEntityStream());

      MediaType mediaType = MediaType.valueOf(contentType);
      final BrowserCache.Entry entry = cache.put(request.getUri().toString(), mediaType,
              response.getHeaders(), cached, expires, etag, lastModified);

      response.setEntityStream(new ByteArrayInputStream(cached));
   }

   protected BrowserCache.Entry getEntry(ClientRequestContext request) throws Exception
   {
      String uri = request.getUri().toString();

      List<MediaType> acceptableMediaTypes = request.getAcceptableMediaTypes();
      BrowserCache.Entry entry = null;
      if (acceptableMediaTypes.size() > 0)
      {
         for (MediaType accept : acceptableMediaTypes)
         {
            entry = cache.get(uri, accept);
            if (entry != null) return entry;
            if (MediaTypeHelper.isTextLike(accept))
            {
               entry = cache.get(uri, accept.withCharset("UTF-8"));
               if (entry != null) return entry;
            }
         }

      }
      else
      {
         return cache.getAny(uri);
      }

      return null;
   }

}
