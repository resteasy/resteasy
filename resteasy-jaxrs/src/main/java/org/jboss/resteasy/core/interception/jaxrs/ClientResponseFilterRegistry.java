package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.client.ClientResponseFilter;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFilterRegistry extends JaxrsInterceptorRegistry<ClientResponseFilter>
{
   public ClientResponseFilterRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ClientResponseFilter.class);
   }

   @Override
   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new DescendingPrecedenceComparator());

   }

   @Override
   public ClientResponseFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ClientResponseFilterRegistry clone = new ClientResponseFilterRegistry(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
