package org.jboss.resteasy.plugins.server.undertow;

import static io.undertow.servlet.Servlets.servlet;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;

import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

public class UndertowEmbededServer implements EmbeddedJaxrsServer
{
   final PathHandler root = new PathHandler();

   final ServletContainer container = ServletContainer.Factory.newInstance();

   private int port = 8080;

   private String hostName = "localhost";

   private ResteasyDeployment deployment;

   private String contextPath = "/";

   private Undertow server;

   @Override
   public void setRootResourcePath(String rootResourcePath)
   {
      this.contextPath = rootResourcePath;

   }

   @Override
   public void start()
   {
      server = Undertow.builder().addHttpListener(port, hostName).setHandler(root).build();
      server.start();
      deploy(this.deployment);

   }

   public void deploy(ResteasyDeployment deployment)
   {
      deploy(deployment, this.contextPath, null, null);
   }

   public void deploy(ResteasyDeployment deployment, String contextPath, Map<String, String> contextParams,
         Map<String, String> initParams)
   {
      if (contextPath == null)
         contextPath = "/";
      if (!contextPath.startsWith("/"))
         contextPath = "/" + contextPath;
      ApplicationPath appPath = this.deployment.getApplication().getClass().getAnnotation(ApplicationPath.class);
      String path = "/";
      if (appPath != null)
         path = appPath.value();
      DeploymentInfo builder = undertowDeployment(deployment, path);
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
      deploy(builder);
   }

   public void deploy(DeploymentInfo builder)
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
   }

   @Override
   public void stop()
   {
      server.stop();

   }

   @Override
   public ResteasyDeployment getDeployment()
   {
      return this.deployment;
   }

   @Override
   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;

   }

   @Override
   public void setSecurityDomain(SecurityDomain sc)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setSSLContext(SSLContext sslContext)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setProtocol(String protocol)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setSslParameters(SSLParameters sslParameters)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setPort(int port)
   {
      this.port = port;

   }

   @Override
   public void setHost(String host)
   {
      this.hostName = host;

   }

   public DeploymentInfo undertowDeployment(ResteasyDeployment deployment, String mapping)
   {
      if (mapping == null)
         mapping = "/";
      if (!mapping.startsWith("/"))
         mapping = "/" + mapping;
      if (!mapping.endsWith("/"))
         mapping += "/";
      mapping = mapping + "*";
      String prefix = null;
      if (!mapping.equals("/*"))
         prefix = mapping.substring(0, mapping.length() - 2);
      ServletInfo resteasyServlet = servlet("ResteasyServlet", HttpServlet30Dispatcher.class).setAsyncSupported(true)
            .setLoadOnStartup(1).addMapping(mapping);
      if (prefix != null)
         resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);

      return new DeploymentInfo().addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
            .addServlet(resteasyServlet);
   }

}
