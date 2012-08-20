package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import javax.ws.rs.GET;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@HeaderDecoratorPrecedence
public class CacheControlFilter implements ContainerResponseFilter
{
   protected CacheControl cacheControl;

   public CacheControlFilter(CacheControl cacheControl)
   {
      this.cacheControl = cacheControl;
   }

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      if (responseContext.getStatus() == 200)
      {
         responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
      }
   }
}