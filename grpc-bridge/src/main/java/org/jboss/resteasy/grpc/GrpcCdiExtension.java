package org.jboss.resteasy.grpc;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

//import javax.enterprise.event.Observes;
//import javax.enterprise.inject.spi.BeanManager;
//import javax.enterprise.inject.spi.BeforeBeanDiscovery;
//import javax.enterprise.inject.spi.Extension;

public class GrpcCdiExtension implements Extension {
   
   static private BeanManager beanManager;

   @SuppressWarnings("static-access")
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager) {
	   System.out.println("GrpcCdiExtension.observeBeforeBeanDiscovery(): beanManager: " + beanManager);
      this.beanManager = beanManager;
   }
   
   public void observeafterBeanDiscovery(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
	   System.out.println("GrpcCdiExtension.observeafterBeanDiscovery(): beanManager: " + beanManager);
	      this.beanManager = beanManager; 
   }
   
   static public BeanManager getBeanManager() {
      return beanManager;
   }
}
