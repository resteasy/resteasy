package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.core.interception.ResourceMethodContext;
import org.jboss.resteasy.core.interception.ResourceMethodInterceptor;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodCacheControlInterceptor implements ResourceMethodInterceptor
{
   protected CacheControl cacheControl;

   public boolean accepted(ResourceMethod resourceMethod)
   {
      if (!resourceMethod.getHttpMethods().contains("GET")) return false;
      Cache cache = resourceMethod.getResourceClass().getAnnotation(Cache.class);
      NoCache nocache = resourceMethod.getResourceClass().getAnnotation(NoCache.class);
      Cache methodCached = resourceMethod.getMethod().getAnnotation(Cache.class);
      NoCache noMethodCache = resourceMethod.getMethod().getAnnotation(NoCache.class);

      if (methodCached != null)
      {
         initCacheControl(methodCached);
      }
      else if (noMethodCache != null)
      {
         cacheControl = new CacheControl();
         cacheControl.setNoCache(true);
      }
      else if (cache != null)
      {
         initCacheControl(methodCached);
      }
      else if (nocache != null)
      {
         cacheControl = new CacheControl();
         cacheControl.setNoCache(true);
         for (String field : nocache.fields()) cacheControl.getNoCacheFields().add(field);
      }

      return cacheControl != null;
   }

   protected void initCacheControl(Cache methodCached)
   {
      cacheControl = new CacheControl();
      if (methodCached.isPrivate())
      {
         cacheControl.setPrivate(true);
      }
      if (methodCached.maxAge() > -1)
      {
         cacheControl.setMaxAge(methodCached.maxAge());
      }
      if (methodCached.sMaxAge() > -1)
      {
         cacheControl.setSMaxAge(methodCached.sMaxAge());
      }
      cacheControl.setMustRevalidate((methodCached.mustRevalidate()));
      cacheControl.setNoStore((methodCached.noStore()));
      cacheControl.setNoTransform((methodCached.noTransform()));
      cacheControl.setProxyRevalidate(methodCached.proxyRevalidate());
   }

   public ServerResponse invoke(ResourceMethodContext ctx) throws Failure, ApplicationException, WebApplicationException
   {

      ServerResponse response = ctx.proceed();
      if (response.getStatus() == 200)
      {
         response.getMetadata().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
         return response;
      }
      return response;
   }
}