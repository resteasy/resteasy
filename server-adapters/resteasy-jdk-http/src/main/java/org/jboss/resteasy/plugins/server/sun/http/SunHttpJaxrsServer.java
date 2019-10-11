package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.EmbeddedServerHelper;
import org.jboss.resteasy.util.PortProvider;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * com.sun.net.httpserver.HttpServer adapter for Resteasy.  You may instead want to create and manage your own HttpServer.
 * Use the HttpContextBuilder class in this case to build and register a specific HttpContext.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SunHttpJaxrsServer implements EmbeddedJaxrsServer<SunHttpJaxrsServer>
{
   protected HttpContextBuilder context = new HttpContextBuilder();
   protected HttpServer httpServer;
   protected int configuredPort = PortProvider.getPort();
   protected int runtimePort = -1;
   protected ResteasyDeployment deployment;
   private EmbeddedServerHelper serverHelper = new EmbeddedServerHelper();

   @Override
   public SunHttpJaxrsServer deploy() {
      // no-op
      return this;
   }

   @Override
   public SunHttpJaxrsServer start()
   {
      serverHelper.checkDeployment(deployment);

      String aPath = serverHelper.checkAppDeployment(deployment);
      if (aPath == null) {
         aPath = context.getPath();
      }

      setRootResourcePath(serverHelper.checkContextPath(aPath));

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
      return this;
   }

   @Override
   public void stop()
   {
      runtimePort = -1;
      httpServer.stop(0);
      context.cleanup();

      if (deployment != null) {
         deployment.stop();
      }
   }

   @Override
   public ResteasyDeployment getDeployment() {
      if(deployment == null) {
         deployment = context.getDeployment();
      }
      return deployment;
   }

   @Override
   public SunHttpJaxrsServer setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
      this.context.setDeployment(deployment);
      return this;
   }
   /**
    * Value is ignored if HttpServer property is set. Default value is 8080
    *
    * @param port
    */
   @Override
   public SunHttpJaxrsServer setPort(int port)
   {
      this.configuredPort = port;
      return this;
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
   public SunHttpJaxrsServer setHostname(String hostname) {
      // no-op
      return this;
   }

   /**
    * Setting a security domain will turn on Basic Authentication
    *
    * @param securityDomain
    */
   @Override
   public SunHttpJaxrsServer setSecurityDomain(SecurityDomain securityDomain)
   {
      this.context.setSecurityDomain(securityDomain);
      return this;
   }


   @Override
   public SunHttpJaxrsServer setRootResourcePath(String rootResourcePath)
   {
      context.setPath(rootResourcePath);
      return this;
   }


   /**
    * If you do not provide an HttpServer instance, one will be created on startup
    *
    * @param httpServer
    */
   public SunHttpJaxrsServer setHttpServer(HttpServer httpServer)
   {
      this.httpServer = httpServer;
      return this;
   }

}
