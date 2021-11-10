package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletDispatcher extends HttpServlet implements HttpRequestFactory, HttpResponseFactory
{
   protected ServletContainerDispatcher servletContainerDispatcher;

   public Dispatcher getDispatcher()
   {
      return servletContainerDispatcher.getDispatcher();
   }


   public void init(ServletConfig servletConfig) throws ServletException
   {
      super.init(servletConfig);
      Map<Class<?>, Object> map = ResteasyContext.getContextDataMap();
      map.put(ServletContext.class, servletConfig.getServletContext());
      map.put(ServletConfig.class, servletConfig);
      servletContainerDispatcher = new ServletContainerDispatcher(servletConfig);
      ServletBootstrap bootstrap = new ServletBootstrap(servletConfig);
      servletContainerDispatcher.init(servletConfig.getServletContext(), bootstrap, this, this);
   }

   @Override
   public void destroy()
   {
      super.destroy();
      servletContainerDispatcher.destroy();
   }

   @Override
   protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      servletContainerDispatcher.service(httpMethod, request, response, true);
   }

   public HttpRequest createResteasyHttpRequest(String httpMethod, HttpServletRequest request, ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response)
   {
      return createHttpRequest(httpMethod, request, headers, uriInfo, theResponse, response);
   }


   public HttpResponse createResteasyHttpResponse(HttpServletResponse response, HttpServletRequest request)
   {
      return createServletResponse(response, request);
   }

   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest request, ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response)
   {
      return new HttpServletInputMessage(request, response, getServletContext(), theResponse, headers, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) getDispatcher());
   }


   protected HttpResponse createServletResponse(HttpServletResponse response, HttpServletRequest request)
   {
      return new HttpServletResponseWrapper(response, request, getDispatcher().getProviderFactory());
   }

}
