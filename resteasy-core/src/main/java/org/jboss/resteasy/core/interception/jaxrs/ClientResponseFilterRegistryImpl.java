package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.ClientResponseFilter;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFilterRegistryImpl extends JaxrsInterceptorRegistryImpl<ClientResponseFilter>
{
   public ClientResponseFilterRegistryImpl(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ClientResponseFilter.class);
   }

   @Override
   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new DescendingPrecedenceComparator());

   }

   @Override
   public ClientResponseFilterRegistryImpl clone(ResteasyProviderFactory factory)
   {
      ClientResponseFilterRegistryImpl clone = new ClientResponseFilterRegistryImpl(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
