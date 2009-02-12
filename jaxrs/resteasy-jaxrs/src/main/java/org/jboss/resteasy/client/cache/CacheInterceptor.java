package org.jboss.resteasy.client.cache;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.core.interception.AcceptedByMethod;
import org.jboss.resteasy.core.interception.ClientExecutionContext;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
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

      BrowserCache.CacheEntry entry = getEntry(request);
      if (entry == null)
      {
         ClientResponse response = ctx.proceed();
         if (response.getStatus() != 200) return response;
         return cache.cacheIfPossible(request, (BaseClientResponse) response);
      }

      if (entry.expired())
      {
         cache.remove(request.getUri());
         BrowserCache.Header[] headers = entry.getValidationHeaders();
         for (BrowserCache.Header header : headers)
         {
            request.header(header.getName(), header.getValue());
         }

         ClientResponse response = ctx.proceed();
         if (response.getStatus() == Response.Status.NOT_MODIFIED.getStatusCode())
         {
            return cache.updateOnNotModified(request, entry, (BaseClientResponse) response);
         }
         else if (response.getStatus() == 200)
         {
            return cache.cacheIfPossible(request, (BaseClientResponse) response);
         }
         else
         {
            return response;
         }
      }
      else
      {
         return new CachedClientResponse(entry, request.getProviderFactory());
      }
   }

   protected BrowserCache.CacheEntry getEntry(ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      Map<MediaType, BrowserCache.CacheEntry> entries = cache.get(uri);
      if (entries == null)
      {
         return null;
      }

      BrowserCache.CacheEntry entry = null;
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
            entry = entries.get(accept);
            if (entry != null) return entry;
         }
      }
      else
      {
         Iterator<BrowserCache.CacheEntry> it = entries.values().iterator();
         if (it.hasNext()) return it.next();
      }

      return null;
   }
}
