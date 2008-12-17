package org.jboss.resteasy.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public abstract class ResteasyWebHandlerTemplate<T>
{
   protected SynchronousDispatcher dispatcher;

   public ResteasyWebHandlerTemplate(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public T handle(ResteasyRequestWrapper requestWrapper,
         HttpServletResponse httpServletResponse) throws Exception
   {

      T result = null;
      HttpResponse response = new HttpServletResponseWrapper(httpServletResponse,
            dispatcher.getProviderFactory());

      HttpServletRequest servletRequest = requestWrapper.getHttpServletRequest();
      try
      {
         ResteasyProviderFactory.pushContext(HttpServletRequest.class,
               servletRequest);
         ResteasyProviderFactory.pushContext(HttpServletResponse.class,
               httpServletResponse);
         ResteasyProviderFactory.pushContext(SecurityContext.class,
               new ServletSecurityContext(servletRequest));

         result = handle(requestWrapper, response);

      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
      return result;
   }

   abstract protected T handle(ResteasyRequestWrapper requestWrapper, HttpResponse response) throws Exception;

}
