package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;

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
   protected ResteasyDeployment deployment = null;

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }


   public void init(ServletConfig servletConfig) throws ServletException
   {
      super.init(servletConfig);
      providerFactory = (ResteasyProviderFactory) servletConfig.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
      dispatcher = (Dispatcher) servletConfig.getServletContext().getAttribute(Dispatcher.class.getName());

      if ((providerFactory != null && dispatcher == null) || (providerFactory == null && dispatcher != null))
      {
         throw new ServletException("Unknown state.  You have a Listener messing up what resteasy expects");
      }

      ServletBootstrap bootstrap = new ServletBootstrap(servletConfig);

      // We haven't been initialized by a Listener already
      if (providerFactory == null)
      {
         deployment = bootstrap.createDeployment();
         deployment.start();

         ServletContext servletContext = servletConfig.getServletContext();

         servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
         servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
         servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());

         dispatcher = deployment.getDispatcher();
         providerFactory = deployment.getProviderFactory();

      }
      else
      {
         String application = servletConfig.getInitParameter("javax.ws.rs.Application");
         if (application != null)
         {
            try
            {
               Application app = (Application) Thread.currentThread().getContextClassLoader().loadClass(application.trim()).newInstance();
               processApplication(app);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      servletMappingPrefix = bootstrap.getParameter("resteasy.servlet.mapping.prefix");
      if (servletMappingPrefix == null) servletMappingPrefix = "";
      servletMappingPrefix = servletMappingPrefix.trim();

      dispatcher.getDefaultContextObjects().put(ServletConfig.class, servletConfig);
      dispatcher.getDefaultContextObjects().put(ServletContext.class, servletConfig.getServletContext());

   }

   protected void processApplication(Application config)
   {
      logger.info("Deploying " + Application.class.getName() + ": " + config.getClass());
      ArrayList<Class> actualResourceClasses = new ArrayList<Class>();
      ArrayList<Class> actualProviderClasses = new ArrayList<Class>();
      ArrayList resources = new ArrayList();
      ArrayList providers = new ArrayList();
      if (config.getClasses() != null)
      {
         for (Class clazz : config.getClasses())
         {
            if (GetRestful.isRootResource(clazz))
            {
               actualResourceClasses.add(clazz);
            }
            else if (clazz.isAnnotationPresent(Provider.class))
            {
               actualProviderClasses.add(clazz);
            }
            else
            {
               throw new RuntimeException("Application.getClasses() returned unknown class type: " + clazz.getName());
            }
         }
      }
      if (config.getSingletons() != null)
      {
         for (Object obj : config.getSingletons())
         {
            if (GetRestful.isRootResource(obj.getClass()))
            {
               logger.info("Adding singleton resource " + obj.getClass().getName() + " from Application " + Application.class.getName());
               resources.add(obj);
            }
            else if (obj.getClass().isAnnotationPresent(Provider.class))
            {
               providers.add(obj);
            }
            else
            {
               throw new RuntimeException("Application.getSingletons() returned unknown class type: " + obj.getClass().getName());
            }
         }
      }
      for (Class clazz : actualProviderClasses) providerFactory.registerProvider(clazz);
      for (Object obj : providers) providerFactory.registerProviderInstance(obj);
      for (Class clazz : actualResourceClasses) dispatcher.getRegistry().addPerRequestResource(clazz);
      for (Object obj : providers) dispatcher.getRegistry().addSingletonResource(obj);
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
         //logger.info("***PATH: " + request.getRequestURL());
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
/*
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
            */
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
