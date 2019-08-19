package org.jboss.resteasy.plugins.server.embedded;

import org.jboss.resteasy.spi.ResteasyDeployment;

public interface EmbeddedJaxrsServer<T> {
   T deploy();

   T start();

   void stop();

   ResteasyDeployment getDeployment();

   T setDeployment(ResteasyDeployment deployment);

   T setPort(int port);

   T setHostname(String hostname);

   T setRootResourcePath(String rootResourcePath);

   T setSecurityDomain(SecurityDomain sc);
}
