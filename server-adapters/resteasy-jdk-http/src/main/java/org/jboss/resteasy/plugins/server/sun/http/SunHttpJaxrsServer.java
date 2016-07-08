package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * com.sun.net.httpserver.HttpServer adapter for Resteasy.  You may instead want to create and manage your own HttpServer.
 * Use the HttpContextBuilder class in this case to build and register a specific HttpContext.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SunHttpJaxrsServer implements EmbeddedJaxrsServer
{
   protected HttpContextBuilder context = new HttpContextBuilder();
   protected HttpServer httpServer;
   protected int configuredPort = 8080;
   protected int runtimePort = -1;

   public void setRootResourcePath(String rootResourcePath)
   {
      context.setPath(rootResourcePath);
   }

   public ResteasyDeployment getDeployment()
   {
      return context.getDeployment();
   }

   public void setDeployment(ResteasyDeployment deployment)
   {
      this.context.setDeployment(deployment);
   }

   /**
    * Setting a security domain will turn on Basic Authentication
    *
    * @param securityDomain
    */
   public void setSecurityDomain(SecurityDomain securityDomain)
   {
      this.context.setSecurityDomain(securityDomain);
   }

   /**
    * If you do not provide an HttpServer instance, one will be created on startup
    *
    * @param httpServer
    */
   public void setHttpServer(HttpServer httpServer)
   {
      this.httpServer = httpServer;
   }

   /**
    * Value is ignored if HttpServer property is set. Default value is 8080
    *
    * @param port
    */
   public void setPort(int port)
   {
      this.configuredPort = port;
   }

   /**
    * Gets port number of this HttpServer.
    *
    * @return port number.
     */
   public int getPort()
   {
      return runtimePort > 0 ? runtimePort : configuredPort;
   }

   @Override
   public void start()
   {
      if (httpServer == null)
      {
         try
         {
            httpServer = HttpServer.create(new InetSocketAddress(configuredPort), 10);
            runtimePort = httpServer.getAddress().getPort();
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      context.bind(httpServer);
      httpServer.start();
   }

   @Override
   public void stop()
   {
      runtimePort = -1;
      httpServer.stop(0);
      context.cleanup();
   }
}
