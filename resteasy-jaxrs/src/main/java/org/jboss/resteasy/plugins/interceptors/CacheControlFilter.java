package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(Priorities.HEADER_DECORATOR)
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