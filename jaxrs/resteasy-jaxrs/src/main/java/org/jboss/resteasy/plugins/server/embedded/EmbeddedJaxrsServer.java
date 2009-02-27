package org.jboss.resteasy.plugins.server.embedded;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface EmbeddedJaxrsServer
{
   void setRootResourcePath(String rootResourcePath);

   void start();

   void stop();

   ResteasyProviderFactory getFactory();

   Registry getRegistry();

   void setSecurityDomain(SecurityDomain sc);

   void addApplicationConfig(Application config);

}
