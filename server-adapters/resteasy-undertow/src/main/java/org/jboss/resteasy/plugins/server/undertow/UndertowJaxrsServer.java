package org.jboss.resteasy.plugins.server.undertow;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;

import org.jboss.resteasy.util.PortProvider;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static io.undertow.servlet.Servlets.servlet;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Wrapper around Undertow to make resteasy deployments easier
 * Each ResteasyDeployment or jaxrs Application is deployed under its own web deployment (WAR)
 *
 * You may also deploy after the server has started.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UndertowJaxrsServer
{
   final PathHandler root = new PathHandler();
   final ServletContainer container = ServletContainer.Factory.newInstance();
   protected Undertow server;

   /**
    * Creates a web deployment for your ResteasyDeployent so you can set up things like security constraints
    * You'd call this method, add your servlet security constraints, then call deploy(DeploymentInfo)
    *
    * Note, only one ResteasyDeployment can be applied per DeploymentInfo
    * ResteasyServlet is mapped to mapping + "/*"
    *
    * Example:
    *
    * DeploymentInfo di = server.undertowDeployment(resteasyDeployment, "rest");
    * di.setDeploymentName("MyDeployment")
    * di.setContextRoot("root");
    * server.deploy(di);
    *
    * @param deployment
    * @param mapping resteasy.servlet.mapping.prefix
    * @return must be deployed by calling deploy(DeploymentInfo), also does not set context path or deployment name
    */
   public DeploymentInfo undertowDeployment(ResteasyDeployment deployment, String mapping)
   {
      if (mapping == null) mapping = "/";
      if (!mapping.startsWith("/")) mapping = "/" + mapping;
      if (!mapping.endsWith("/")) mapping += "/";
      mapping = mapping + "*";
      String prefix = null;
      if (!mapping.equals("/*")) prefix = mapping.substring(0, mapping.length() - 2);
      ServletInfo resteasyServlet = servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
              .setAsyncSupported(true)
              .setLoadOnStartup(1)
              .addMapping(mapping);
      if (prefix != null) resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);

      return  new DeploymentInfo()
              .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
              .addServlet(
                      resteasyServlet
                         );
   }

   /**
    * Creates a web deployment for your ResteasyDeployent so you can set up things like security constraints
    * You'd call this method, add your servlet security constraints, then call deploy(DeploymentInfo)
    *
    * Note, only one ResteasyDeployment can be applied per DeploymentInfo.  Resteasy servlet is mapped to "/*"
    *
    * @param deployment
    * @return
    */
   public DeploymentInfo undertowDeployment(ResteasyDeployment deployment)
   {
      return undertowDeployment(deployment, "/");
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will ignore any @ApplicationPath annotation.
    *
    * @param application
    * @param mapping resteasy.servlet.mapping.prefix
    * @return
    */
   public DeploymentInfo undertowDeployment(Class<? extends Application> application, String mapping)
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplicationClass(application.getName());
      DeploymentInfo di = undertowDeployment(deployment, mapping);
      di.setClassLoader(application.getClassLoader());
      return di;
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will bind the resteasy.servlet.mapping.prefix
    * to @ApplicationPath if it exists, otherwise "/".
    *
    * @param application
    * @return
    */
   public DeploymentInfo undertowDeployment(Class<? extends Application> application)
   {
      ApplicationPath appPath = application.getAnnotation(ApplicationPath.class);
      String path = "/";
      if (appPath != null) path = appPath.value();
      return undertowDeployment(application, path);
   }
   
   /**
    * Maps a path prefix to a resource handler to allow serving resources other than the JAX-RS endpoints.
    * For example, this can be used for serving static resources like web pages or API documentation that might 
    * be deployed with the REST application server. 
    * 
    * @param path 
    * @param handler
    */
   public void addResourcePrefixPath(String path, ResourceHandler handler) 
   {
      root.addPrefixPath(path, handler);
   }

   /**
    * Creates a web deployment under "/"
    *
    * @param deployment
    * @return
    */
   public UndertowJaxrsServer deploy(ResteasyDeployment deployment)
   {
      return deploy(deployment, "/");
   }

   /**
    * Creates a web deployment under contextPath
    *
    * @param deployment
    * @param contextPath
    * @return
    */
   public UndertowJaxrsServer deploy(ResteasyDeployment deployment, String contextPath)
   {
      return deploy(deployment, contextPath, null, null);
   }
   
   public UndertowJaxrsServer deploy(ResteasyDeployment deployment, String contextPath, Map<String, String> contextParams, Map<String, String> initParams)
   {
      if (contextPath == null) contextPath = "/";
      if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
      DeploymentInfo builder = undertowDeployment(deployment);
      builder.setContextPath(contextPath);
      builder.setDeploymentName("Resteasy" + contextPath);
      builder.setClassLoader(deployment.getApplication().getClass().getClassLoader());
      if (contextParams != null)
      {
         for (Entry<String, String> e : contextParams.entrySet())
         {
            builder.addInitParameter(e.getKey(), e.getValue());
         }
      }  
      if (initParams != null)
      {
         ServletInfo servletInfo = builder.getServlets().get("ResteasyServlet");
         for (Entry<String, String> e : initParams.entrySet())
         {
            servletInfo.addInitParam(e.getKey(), e.getValue());
         }
      }
      return deploy(builder);
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will bind the contextPath
    * to @ApplicationPath if it exists, otherwise "/".
    *
    * @param application
    * @return
    */
   public UndertowJaxrsServer deploy(Class<? extends Application> application)
   {
      ApplicationPath appPath = application.getAnnotation(ApplicationPath.class);
      String path = "/";
      if (appPath != null) path = appPath.value();
      return deploy(application, path);
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will ignore any @ApplicationPath annotation.
    *
    * @param application
    * @param contextPath
    * @return
    */
   public UndertowJaxrsServer deploy(Class<? extends Application> application, String contextPath)
   {
      if (contextPath == null) contextPath = "/";
      if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplicationClass(application.getName());
      DeploymentInfo di = undertowDeployment(deployment);
      di.setClassLoader(application.getClassLoader());
      di.setContextPath(contextPath);
      di.setDeploymentName("Resteasy" + contextPath);
      return deploy(di);
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will bind the contextPath
    * to @ApplicationPath if it exists, otherwise "/".
    *
    * @param application
    * @return
    */
   public UndertowJaxrsServer deploy(Application application)
   {
      ApplicationPath appPath = application.getClass().getAnnotation(ApplicationPath.class);
      String path = "/";
      if (appPath != null) path = appPath.value();
      return deploy(application, path);
   }

   /**
    * Creates a web deployment for the jaxrs Application.  Will ignore any @ApplicationPath annotation.
    *
    * @param application
    * @param contextPath
    * @return
    */
   public UndertowJaxrsServer deploy(Application application, String contextPath)
   {
      if (contextPath == null) contextPath = "/";
      if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplication(application);
      DeploymentInfo di = undertowDeployment(deployment);
      di.setClassLoader(application.getClass().getClassLoader());
      di.setContextPath(contextPath);
      di.setDeploymentName("Resteasy" + contextPath);
      return deploy(di);
   }


   /**
    * Adds an arbitrary web deployment to underlying Undertow server.  This is for your own deployments
    *
    * @param builder
    * @return
    */
   public UndertowJaxrsServer deploy(DeploymentInfo builder)
   {
      DeploymentManager manager = container.addDeployment(builder);
      manager.deploy();
      try
      {
         root.addPrefixPath(builder.getContextPath(), manager.start());
      }
      catch (ServletException e)
      {
         throw new RuntimeException(e);
      }
      return this;
   }

   public UndertowJaxrsServer start(Undertow.Builder builder)
   {
      server = builder.setHandler(root).build();
      server.start();
      return this;
   }

   public UndertowJaxrsServer start()
   {
      server = Undertow.builder()
              .addHttpListener(PortProvider.getPort(), "localhost")
              .setHandler(root)
              .build();
      server.start();
      return this;
   }

   public void stop()
   {
      server.stop();
   }

}
