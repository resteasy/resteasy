package org.jboss.resteasy.plugins.interceptors;

import javax.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Priority(Priorities.HEADER_DECORATOR)
public class CacheControlFilter implements ContainerResponseFilter
{
   protected CacheControl cacheControl;

   public CacheControlFilter(final CacheControl cacheControl)
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
