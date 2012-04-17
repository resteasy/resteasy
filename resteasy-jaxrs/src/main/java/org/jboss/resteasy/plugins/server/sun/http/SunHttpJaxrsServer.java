package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SunHttpJaxrsServer implements EmbeddedJaxrsServer
{
   protected ResteasyContext context = new ResteasyContext();
   protected HttpServer httpServer;
   protected int port = 8080;

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

   public void setSecurityDomain(SecurityDomain securityDomain)
   {
      this.context.setSecurityDomain(securityDomain);
   }

   public void setHttpServer(HttpServer httpServer)
   {
      this.httpServer = httpServer;
   }

   /**
    * Value is ignored if HttpServer property is set
    *
    * @param port
    */
   public void setPort(int port)
   {
      this.port = port;
   }

   @Override
   public void start()
   {
      if (httpServer == null)
      {
         try
         {
            httpServer = HttpServer.create(new InetSocketAddress(port), 10);
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
      httpServer.stop(0);
      context.cleanup();
   }
}
