package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FilterDispatcher implements Filter, HttpRequestFactory, HttpResponseFactory
{
   protected ServletContainerDispatcher servletContainerDispatcher;
   protected ServletContext servletContext;

   public Dispatcher getDispatcher()
   {
      return servletContainerDispatcher.getDispatcher();
   }


   public void init(FilterConfig servletConfig) throws ServletException
   {
      Map<Class<?>, Object> map = ResteasyContext.getContextDataMap();
      map.put(ServletContext.class, servletConfig.getServletContext());
      map.put(FilterConfig.class, servletConfig);
      servletContainerDispatcher = new ServletContainerDispatcher();
      FilterBootstrap bootstrap = new FilterBootstrap(servletConfig);
      servletContext = servletConfig.getServletContext();
      servletContainerDispatcher.init(servletContext, bootstrap, this, this);
      servletContainerDispatcher.getDispatcher().getDefaultContextObjects().put(FilterConfig.class, servletConfig);

   }

   public HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request, ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response)
   {
      return new HttpServletInputMessage(request, response, servletContext, theResponse, headers, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) getDispatcher());
   }


   public HttpResponse createResteasyHttpResponse(HttpServletResponse response, HttpServletRequest request)
   {
      return new HttpServletResponseWrapper(response, request, getDispatcher().getProviderFactory());
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
   {
      try
      {
         servletContainerDispatcher.service(((HttpServletRequest) servletRequest).getMethod(), (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, false);
      }
      catch (NotFoundException e)
      {
         filterChain.doFilter(servletRequest, servletResponse);
      }
   }

   public void destroy()
   {
      servletContainerDispatcher.destroy();
   }


}
