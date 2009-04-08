package org.jboss.resteasy.client.cache;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.core.interception.AcceptedByMethod;
import org.jboss.resteasy.core.interception.ClientExecutionContext;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class CacheInterceptor implements ClientExecutionInterceptor, AcceptedByMethod
{
   protected BrowserCache cache;

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
      
      return new CachedClientResponse(entry, request.getProviderFactory());
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


      BrowserCache.Entry entry = cache.put(request.getUri(), old.getMediaType(), old.getHeaders(), old.getCached(), expires, etag, lastModified);
      return new CachedClientResponse(entry, request.getProviderFactory());

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

      BrowserCache.Entry entry = cache.put(request.getUri(), MediaType.valueOf(contentType), (MultivaluedMap<String, String>) response.getHeaders(), cached, expires, etag, lastModified);

      return new CachedClientResponse(entry, request.getProviderFactory());
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
