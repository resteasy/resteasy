package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import javax.ws.rs.GET;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@HeaderDecoratorPrecedence
public class CacheControlInterceptor implements PostProcessInterceptor, AcceptedByMethod
{
   protected CacheControl cacheControl;

   public boolean accept(Class declaring, Method method)
   {
      if (declaring == null || method == null) return false;

      if (!method.isAnnotationPresent(GET.class)) return false;
      Cache cache = (Cache) declaring.getAnnotation(Cache.class);
      NoCache nocache = (NoCache) declaring.getAnnotation(NoCache.class);
      Cache methodCached = method.getAnnotation(Cache.class);
      NoCache noMethodCache = method.getAnnotation(NoCache.class);

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
         initCacheControl(cache);
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

   public void postProcess(ServerResponse response)
   {
      if (response != null && response.getStatus() == 200)
      {
         response.getMetadata().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
      }
   }
}