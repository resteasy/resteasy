package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
      servletContainerDispatcher = new ServletContainerDispatcher();
      ServletBootstrap bootstrap = new ServletBootstrap(servletConfig);
      servletContainerDispatcher.init(servletConfig.getServletContext(), bootstrap, this, this);
      servletContainerDispatcher.getDispatcher().getDefaultContextObjects().put(ServletConfig.class, servletConfig);

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


   public HttpResponse createResteasyHttpResponse(HttpServletResponse response)
   {
      return createServletResponse(response);
   }

   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest request, ResteasyHttpHeaders headers, ResteasyUriInfo uriInfo, HttpResponse theResponse, HttpServletResponse response)
   {
      return new HttpServletInputMessage(request, response, getServletContext(), theResponse, headers, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) getDispatcher());
   }


   protected HttpResponse createServletResponse(HttpServletResponse response)
   {
      return new HttpServletResponseWrapper(response, getDispatcher().getProviderFactory());
   }

}
