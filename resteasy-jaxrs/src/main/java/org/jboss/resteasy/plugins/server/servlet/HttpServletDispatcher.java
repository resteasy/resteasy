package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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
   protected ResteasyProviderFactory providerFactory;
   private final static Logger logger = LoggerFactory.getLogger(HttpServletDispatcher.class);
   private String servletMappingPrefix = "";

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }


   public void init(ServletConfig servletConfig) throws ServletException
   {
      providerFactory = (ResteasyProviderFactory) servletConfig.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
      {
         providerFactory = new ResteasyProviderFactory();
         servletConfig.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), providerFactory);
      }

      dispatcher = (Dispatcher) servletConfig.getServletContext().getAttribute(Dispatcher.class.getName());
      if (dispatcher == null)
      {
         dispatcher = new SynchronousDispatcher(providerFactory);
         servletConfig.getServletContext().setAttribute(Dispatcher.class.getName(), dispatcher);
         servletConfig.getServletContext().setAttribute(Registry.class.getName(), dispatcher.getRegistry());
      }
      servletMappingPrefix = servletConfig.getServletContext().getInitParameter("resteasy.servlet.mapping.prefix");
      if (servletMappingPrefix == null) servletMappingPrefix = "";
      servletMappingPrefix = servletMappingPrefix.trim();
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      try
      {
         // classloader/deployment aware RestasyProviderFactory.  Used to have request specific
         // ResteasyProviderFactory.getInstance()
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.push(providerFactory);
         }
         HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
         UriInfoImpl uriInfo = ServletUtil.extractUriInfo(request, servletMappingPrefix);

         HttpResponse theResponse = createServletResponse(response);
         HttpRequest in = createHttpRequest(httpMethod, request, headers, uriInfo, theResponse);

         try
         {
            ResteasyProviderFactory.pushContext(HttpServletRequest.class, request);
            ResteasyProviderFactory.pushContext(HttpServletResponse.class, response);
            try
            {
               // embedded TJWS and Jetty might not have these things initialized
               ServletConfig config1 = getServletConfig();
               ResteasyProviderFactory.pushContext(ServletConfig.class, config1);
            }
            catch (Exception ignored)
            {
            }
            try
            {
               // embedded TJWS and Jetty might not have these things initialized
               ServletContext servletContext = getServletContext();
               ResteasyProviderFactory.pushContext(ServletContext.class, servletContext);
            }
            catch (Exception ignored)
            {

            }
            ResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(request));
            dispatcher.invoke(in, theResponse);
         }
         finally
         {
            ResteasyProviderFactory.clearContextData();
         }
      }
      finally
      {
         ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
         if (defaultInstance instanceof ThreadLocalResteasyProviderFactory)
         {
            ThreadLocalResteasyProviderFactory.pop();
         }

      }
   }

   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest request, HttpHeaders headers, UriInfoImpl uriInfo, HttpResponse theResponse)
   {
      return new HttpServletInputMessage(request, theResponse, headers, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) dispatcher);
   }

   protected HttpResponse createServletResponse(HttpServletResponse response)
   {
      return new HttpServletResponseWrapper(response, this.dispatcher.getProviderFactory());
   }

}
