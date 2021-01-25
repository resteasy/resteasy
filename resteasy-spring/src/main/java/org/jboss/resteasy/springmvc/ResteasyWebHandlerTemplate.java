package org.jboss.resteasy.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public abstract class ResteasyWebHandlerTemplate<T>
{
   protected ResteasyProviderFactory factory;

   public ResteasyWebHandlerTemplate(final ResteasyProviderFactory factory)
   {
      this.factory = factory;
   }

   public T handle(ResteasyRequestWrapper requestWrapper,
                   HttpServletResponse httpServletResponse) throws Exception
   {

      T result = null;
      HttpResponse response = new HttpServletResponseWrapper(httpServletResponse, requestWrapper.getHttpServletRequest(),
              factory);

      HttpServletRequest servletRequest = requestWrapper.getHttpServletRequest();
      try
      {
         ResteasyContext.pushContext(HttpServletRequest.class,
                 servletRequest);
         ResteasyContext.pushContext(HttpServletResponse.class,
                 httpServletResponse);
         ResteasyContext.pushContext(SecurityContext.class,
                 new ServletSecurityContext(servletRequest));

         result = handle(requestWrapper, response);

      }
      finally
      {
         ResteasyContext.clearContextData();
      }
      return result;
   }

   protected abstract T handle(ResteasyRequestWrapper requestWrapper, HttpResponse response) throws Exception;

}
