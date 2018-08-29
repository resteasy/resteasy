package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;

/**
 * Created by rsearls on 8/4/17.
 */
public class ClientRequestFilterRegistry extends JaxrsInterceptorRegistry<ClientRequestFilter> {

   //private static final Logger LOGGER = Logger.getLogger(ClientRequestFilterRegistry.class.getName());

   public ClientRequestFilterRegistry(ResteasyProviderFactory providerFactory)
   {
      super(providerFactory, ClientRequestFilter.class);
   }

   @Override
   public synchronized void registerClass(Class<? extends ClientRequestFilter> declaring)
   {
      checkPreMaching(declaring);
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring)
      {
         @Override
         public Match preMatch()
         {
            return null;
         }
      };
      factory.setIgnorePrematch(true);
      register(factory);
   }

   @Override
   public synchronized void registerClass(Class<? extends ClientRequestFilter> declaring, int priority)
   {
      checkPreMaching(declaring);
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring)
      {
         @Override
         public Match preMatch()
         {
            return null;
         }
      };
      factory.setIgnorePrematch(true);
      factory.setOrder(priority);
      register(factory);
   }

   private void checkPreMaching(Class declaring) {
      if (declaring.isAnnotationPresent(PreMatching.class)) {
         LogMessages.LOGGER.warningPreMatchingSupport(declaring.getName());
      }
   }

   @Override
   public ClientRequestFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ClientRequestFilterRegistry clone = new ClientRequestFilterRegistry(factory);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
