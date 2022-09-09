package org.jboss.resteasy.grpc.runtime;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

public class GrpcCdiExtension implements Extension {

   private static BeanManager beanManager;

   @SuppressWarnings("static-access")
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager) {
      this.beanManager = beanManager;
   }

   public static BeanManager getBeanManager() {
      return beanManager;
   }
}
