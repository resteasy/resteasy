package org.jboss.resteasy.core.interception.jaxrs;

import java.lang.reflect.AccessibleObject;

import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerRequestFilterRegistry extends JaxrsInterceptorRegistry<ContainerRequestFilter>
{
   public ContainerRequestFilterRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ContainerRequestFilter.class);
   }

   public ContainerRequestFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ContainerRequestFilterRegistry clone = new ContainerRequestFilterRegistry(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   @Override
   public ContainerRequestFilter[] postMatch(Class declaring, AccessibleObject target)
   {
      return super.postMatch(declaring, target);
   }
}
