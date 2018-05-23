package org.jboss.resteasy.client.cache;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated Caching in the Resteasy client framework in resteasy-jaxrs is replaced by 
 * caching in the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ResteasyClient
 * @see org.jboss.resteasy.client.jaxrs.cache.CacheInterceptor
 */
@Deprecated
@SuppressWarnings("unchecked")
public class CacheInterceptor implements ClientExecutionInterceptor, AcceptedByMethod
{
   protected BrowserCache cache;

   static class CachedStreamFactory implements BaseClientResponseStreamFactory
   {
      BrowserCache.Entry entry;

      public CachedStreamFactory(BrowserCache.Entry entry)
      {
         this.entry = entry;
      }

      public InputStream getInputStream() throws IOException
      {
         return new ByteArrayInputStream(entry.getCached());
      }

      public void performReleaseConnection()
      {
      }
   }

   public CacheInterceptor(BrowserCache cache)
   {
      this.cache = cache;
   }

   public boolean accept(Class declaring, Method method)
   {
      if (declaring == null || method == null) return true;
      return method.isAnnotationPresent(GET.class);
   }

   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      ClientRequest request = ctx.getRequest();
      if (!request.getHttpMethod().equals("GET"))
      {
         return ctx.proceed();
      }

      BrowserCache.Entry entry = getEntry(request);
      if (entry == null)
      {
         return cache(request, ctx.proceed());
      }

      if (entry.expired())
      {
         cache.remove(request.getUri(), entry.getMediaType());
         BrowserCache.Header[] headers = entry.getValidationHeaders();
         for (BrowserCache.Header header : headers)
         {
            request.header(header.getName(), header.getValue());
         }
         return handleExpired(ctx, request, entry);
      }

      return createClientResponse(request, entry);
   }

   protected ClientResponse handleExpired(ClientExecutionContext ctx,
                                          ClientRequest request, BrowserCache.Entry entry) throws Exception
   {
      ClientResponse response = ctx.proceed();
      if (response.getStatus() == Response.Status.NOT_MODIFIED.getStatusCode())
      {
         return updateOnNotModified(request, entry, (BaseClientResponse) response);
      }
      return cache(request, response);
   }

   private ClientResponse cache(ClientRequest request, ClientResponse response)
           throws Exception
   {
      if (response.getStatus() != 200) return response;
      return cacheIfPossible(request, (BaseClientResponse) response);
   }

   public ClientResponse updateOnNotModified(ClientRequest request, BrowserCache.Entry old, BaseClientResponse response) throws Exception
   {
      old.getHeaders().remove(HttpHeaders.CACHE_CONTROL);
      old.getHeaders().remove(HttpHeaders.EXPIRES);
      old.getHeaders().remove(HttpHeaders.LAST_MODIFIED);
      String cc = (String) response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getResponseHeaders().getFirst(HttpHeaders.EXPIRES);
      int expires = -1;

      if (cc != null)
      {
         CacheControl cacheControl = CacheControl.valueOf(cc);
         if (cacheControl.isNoCache())
         {
            return createClientResponse(request, old);
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

      String lastModified = (String) response.getResponseHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getResponseHeaders().getFirst(HttpHeaders.ETAG);

      if (etag == null) etag = old.getHeaders().getFirst(HttpHeaders.ETAG);
      else old.getHeaders().putSingle(HttpHeaders.ETAG, etag);

      if (lastModified != null)
      {
         old.getHeaders().putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
      }

      if (etag == null && lastModified == null && cc == null && exp == null) // don't cache
      {
         return createClientResponse(request, old);
      }


      BrowserCache.Entry entry = cache.put(request.getUri(), old.getMediaType(), old.getHeaders(), old.getCached(), expires, etag, lastModified);
      return createClientResponse(request, entry);

   }

   private BaseClientResponse createClientResponse(ClientRequest request, BrowserCache.Entry entry)
   {
      BaseClientResponse response = new BaseClientResponse(new CachedStreamFactory(entry));
      response.setStatus(200);
      response.setHeaders(entry.getHeaders());
      response.setProviderFactory(request.getProviderFactory());
      return response;
   }


   public ClientResponse cacheIfPossible(ClientRequest request, BaseClientResponse response) throws Exception
   {
      String cc = (String) response.getResponseHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
      String exp = (String) response.getResponseHeaders().getFirst(HttpHeaders.EXPIRES);
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

      String lastModified = (String) response.getResponseHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
      String etag = (String) response.getResponseHeaders().getFirst(HttpHeaders.ETAG);

      String contentType = (String) response.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

      byte[] cached = ReadFromStream.readFromStream(1024, response.getStreamFactory().getInputStream());
      response.getStreamFactory().performReleaseConnection();

      MediaType mediaType = MediaType.valueOf(contentType);
      final BrowserCache.Entry entry = cache.put(request.getUri(), mediaType,
              response.getResponseHeaders(), cached, expires, etag, lastModified);

      response.setStreamFactory(new CachedStreamFactory(entry));

      return response;
   }

   protected BrowserCache.Entry getEntry(ClientRequest request) throws Exception
   {
      String uri = request.getUri();

      BrowserCache.Entry entry = null;
      String acceptHeader = request.getHeaders().getFirst(HttpHeaders.ACCEPT);
      if (acceptHeader != null)
      {
         List<WeightedMediaType> waccepts = new ArrayList<WeightedMediaType>();
         String[] split = acceptHeader.split(",");
         for (String accept : split)
         {
            waccepts.add(WeightedMediaType.valueOf(accept));
         }
         Collections.sort(waccepts);
         List<MediaType> accepts = new ArrayList<MediaType>();
         for (WeightedMediaType accept : waccepts)
         {
            accepts.add(new MediaType(accept.getType(), accept.getSubtype(), accept.getParameters()));
         }
         for (MediaType accept : accepts)
         {
            entry = cache.get(uri, accept);
            if (entry != null) return entry;
         }
      }
      else
      {
         return cache.getAny(uri);
      }

      return null;
   }
}
