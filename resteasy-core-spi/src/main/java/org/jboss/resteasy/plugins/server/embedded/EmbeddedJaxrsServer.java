package org.jboss.resteasy.plugins.server.embedded;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.ws.rs.JAXRS;

import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface EmbeddedJaxrsServer
{
   final int DEFAULT_PORT = 8080;
   void setRootResourcePath(String rootResourcePath);

   void start();

   void stop();

   ResteasyDeployment getDeployment();

   void setDeployment(ResteasyDeployment deployment);

   void setSecurityDomain(SecurityDomain sc);
   
   void setSSLContext(SSLContext sslContext);
   
   void setProtocol(String protocol);
   
   void setSslParameters(SSLParameters sslParameters);
   
   void setPort(int port);
   
   void setHost(String host);
   
   default void setConfiguration(JAXRS.Configuration configuration) {
   }
   
   default int scanPort() {
      return PortProvider.getFreePort();
   }
}
