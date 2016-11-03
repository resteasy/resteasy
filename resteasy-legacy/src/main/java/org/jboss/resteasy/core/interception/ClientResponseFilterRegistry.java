package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistry instead.
 */
@Deprecated
public class ClientResponseFilterRegistry extends org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistry
{

   public ClientResponseFilterRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory);
   }
}
