package org.resteasy.plugins.server.servlet;

import org.resteasy.Dispatcher;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletDispatcher extends HttpServlet
{
   protected Dispatcher dispatcher;

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }


   public void init(ServletConfig servletConfig) throws ServletException
   {
      ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) servletConfig.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
      {
         providerFactory = new ResteasyProviderFactory();
         servletConfig.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), providerFactory);
      }

      dispatcher = (Dispatcher) servletConfig.getServletContext().getAttribute(Dispatcher.class.getName());
      if (dispatcher == null)
      {
         dispatcher = new Dispatcher(providerFactory);
         servletConfig.getServletContext().setAttribute(Dispatcher.class.getName(), dispatcher);
         servletConfig.getServletContext().setAttribute(Registry.class.getName(), dispatcher.getRegistry());
      }
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
   }

   /**
    * wrapper around service so we can test easily
    *
    * @param httpServletRequest
    * @param httpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   public void invoke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest, httpServletResponse);
   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
      UriInfoImpl uriInfo = ServletUtil.extractUriInfo(request);

      HttpRequest in;
      try
      {
         in = new HttpServletInputMessage(headers, request.getInputStream(), uriInfo, httpMethod.toUpperCase());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      HttpResponse theResponse = new HttpServletResponseWrapper(response, dispatcher.getProviderFactory());

      try
      {
         ResteasyProviderFactory.pushContext(HttpServletRequest.class, request);
         ResteasyProviderFactory.pushContext(HttpServletResponse.class, response);
         ResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(request));
         dispatcher.invoke(in, theResponse);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
   }

}
