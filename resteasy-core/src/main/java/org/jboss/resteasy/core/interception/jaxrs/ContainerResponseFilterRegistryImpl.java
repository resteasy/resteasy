package org.jboss.resteasy.core.interception.jaxrs;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.container.ContainerResponseFilter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerResponseFilterRegistryImpl extends JaxrsInterceptorRegistryImpl<ContainerResponseFilter>
{
   public ContainerResponseFilterRegistryImpl(final ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ContainerResponseFilter.class);
   }

   @Override
   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new DescendingPrecedenceComparator());

   }

   public ContainerResponseFilterRegistryImpl clone(ResteasyProviderFactory factory)
   {
      ContainerResponseFilterRegistryImpl clone = new ContainerResponseFilterRegistryImpl(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   @Override
   public synchronized void registerClass(Class<? extends ContainerResponseFilter> declaring)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      factory.setIgnorePrematch(true);
      register(factory);
   }

   @Override
   public synchronized void registerClass(Class<? extends ContainerResponseFilter> declaring, int priority)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      factory.setIgnorePrematch(true);
      factory.setOrder(priority);
      register(factory);
   }

   @Override
   public synchronized void registerSingleton(ContainerResponseFilter interceptor)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      factory.setIgnorePrematch(true);
      register(factory);
   }

   @Override
   public synchronized void registerSingleton(ContainerResponseFilter interceptor, int priority)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      factory.setIgnorePrematch(true);
      factory.setOrder(priority);
      register(factory);
   }
}
