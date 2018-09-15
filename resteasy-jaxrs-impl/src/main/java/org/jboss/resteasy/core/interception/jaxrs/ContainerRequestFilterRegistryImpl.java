package org.jboss.resteasy.core.interception.jaxrs;

import java.lang.reflect.AccessibleObject;

import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerRequestFilterRegistryImpl extends JaxrsInterceptorRegistryImpl<ContainerRequestFilter>
{
   public ContainerRequestFilterRegistryImpl(ResteasyProviderFactory providerFactory)
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
