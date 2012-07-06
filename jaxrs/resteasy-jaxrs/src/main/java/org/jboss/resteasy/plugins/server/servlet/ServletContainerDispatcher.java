package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletContainerDispatcher
{
   protected Dispatcher dispatcher;
   protected ResteasyProviderFactory providerFactory;
   private final static Logger logger = Logger.getLogger(ServletContainerDispatcher.class);
   private String servletMappingPrefix = "";
   protected ResteasyDeployment deployment = null;
   protected HttpRequestFactory requestFactory;
   protected HttpResponseFactory responseFactory;

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }


   public void init(ServletContext servletContext, ConfigurationBootstrap bootstrap, HttpRequestFactory requestFactory, HttpResponseFactory responseFactory) throws ServletException
   {
      this.requestFactory = requestFactory;
      this.responseFactory = responseFactory;
      providerFactory = (ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());
      dispatcher = (Dispatcher) servletContext.getAttribute(Dispatcher.class.getName());

      if ((providerFactory != null && dispatcher == null) || (providerFactory == null && dispatcher != null))
      {
         throw new ServletException("Unknown state.  You have a Listener messing up what resteasy expects");
      }


      // We haven't been initialized by an external entity so bootstrap ourselves
      if (providerFactory == null)
      {
         deployment = bootstrap.createDeployment();
         deployment.start();

         servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
         servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
         servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());

         dispatcher = deployment.getDispatcher();
         providerFactory = deployment.getProviderFactory();

      }
      else
      {
         // ResteasyBootstrap inited us.  Check to see if the servlet defines an Application class
         String application = bootstrap.getInitParameter("javax.ws.rs.Application");
         if (application != null)
         {
            try
            {
               Application app = ResteasyDeployment.createApplication(application.trim(), providerFactory);
               dispatcher.getDefaultContextObjects().put(Application.class, app);
               // push context data so we can inject it
               Map contextDataMap = ResteasyProviderFactory.getContextDataMap();
               contextDataMap.putAll(dispatcher.getDefaultContextObjects());
               processApplication(app);
            }
            finally
            {
               ResteasyProviderFactory.removeContextDataLevel();
            }
         }
      }
      servletMappingPrefix = bootstrap.getParameter("resteasy.servlet.mapping.prefix");
      if (servletMappingPrefix == null) servletMappingPrefix = "";
      servletMappingPrefix = servletMappingPrefix.trim();


   }

   public void destroy()
   {
      if (deployment != null)
      {
         deployment.stop();
      }
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
               logger.info("Adding singleton provider " + obj.getClass().getName() + " from Application " + Application.class.getName());
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
      for (Object obj : resources) dispatcher.getRegistry().addSingletonResource(obj);
   }


   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response, boolean handleNotFound) throws IOException, NotFoundException
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
         HttpHeaders headers = null;
         ResteasyUriInfo uriInfo = null;
         try
         {
            headers = ServletUtil.extractHttpHeaders(request);
            uriInfo = ServletUtil.extractUriInfo(request, servletMappingPrefix);
         }
         catch (Exception e)
         {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            // made it warn so that people can filter this.
            logger.warn("Failed to parse request.", e);
            return;
         }

         HttpResponse theResponse = responseFactory.createResteasyHttpResponse(response);
         HttpRequest in = requestFactory.createResteasyHttpRequest(httpMethod, request, headers, uriInfo, theResponse, response);

         try
         {
            ResteasyProviderFactory.pushContext(HttpServletRequest.class, request);
            ResteasyProviderFactory.pushContext(HttpServletResponse.class, response);

            ResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(request));
            if (handleNotFound)
            {
               dispatcher.invoke(in, theResponse);
            }
            else
            {
               ((SynchronousDispatcher) dispatcher).invokePropagateNotFound(in, theResponse);
            }
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
}