package org.jboss.resteasy.plugins.server.embedded;

import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface EmbeddedJaxrsServer
{
   void setRootResourcePath(String rootResourcePath);

   void start();

   void stop();

   ResteasyDeployment getDeployment();

   void setDeployment(ResteasyDeployment deployment);

   void setSecurityDomain(SecurityDomain sc);
}
