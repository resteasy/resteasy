package org.resteasy.plugins.server.embedded;

import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

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
}
