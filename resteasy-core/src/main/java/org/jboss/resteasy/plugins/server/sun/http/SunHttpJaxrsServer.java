package org.jboss.resteasy.plugins.server.sun.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.EmbeddedServerHelper;
import org.jboss.resteasy.util.PortProvider;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

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
   protected String host;
   protected SSLContext sslContext;
   protected String protocol;
   protected ResteasyDeployment deployment;
   private EmbeddedServerHelper serverHelper = new EmbeddedServerHelper();

   @Override
   public SunHttpJaxrsServer deploy() {
      // no-op
      return this;
   }
   /**
    * Value is ignored if HttpServer property is set. If host is not set, host
    * will be any local address
    *
    * @param host
    */
   public void setHost(String host)
   {
      this.host = host;
   }

   /**
    * Gets host of this HttpServer
    *
    * @return host
    */
   public String getHost()
   {
      return this.host;
   }

   public SSLContext getSSLContext()
   {
      return this.sslContext;
   }

   public void setSSLContext(SSLContext sslContext)
   {
      this.sslContext = sslContext;
   }

   public String getProtocol()
   {
      return protocol;
   }

   public void setProtocol(String protocol)
   {
      this.protocol = protocol;
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
            InetSocketAddress address = null;
            if (host == null) {
               address = new InetSocketAddress(configuredPort);
            } else {
               address = new InetSocketAddress(host, configuredPort);
            }
            if ("HTTPS".equalsIgnoreCase(protocol) || this.sslContext != null) {
               HttpsServer sslServer = HttpsServer.create(address, 10);
               sslServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));
               httpServer = sslServer;
            } else {
               httpServer = HttpServer.create(address, 10);
            }
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
      // Stop with an arbitrary 10 second delay. This was taken from the VertxJaxrsServer.
      httpServer.stop(10);
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
