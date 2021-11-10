package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import jakarta.ws.rs.container.ContainerRequestFilter;
import java.lang.reflect.AccessibleObject;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerRequestFilterRegistryImpl extends JaxrsInterceptorRegistryImpl<ContainerRequestFilter>
{
   public ContainerRequestFilterRegistryImpl(final ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ContainerRequestFilter.class);
   }

   public ContainerRequestFilterRegistryImpl clone(ResteasyProviderFactory factory)
   {
      ContainerRequestFilterRegistryImpl clone = new ContainerRequestFilterRegistryImpl(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   @SuppressWarnings("rawtypes")
   @Override
   public ContainerRequestFilter[] postMatch(Class declaring, AccessibleObject target)
   {
      return super.postMatch(declaring, target);
   }
}
