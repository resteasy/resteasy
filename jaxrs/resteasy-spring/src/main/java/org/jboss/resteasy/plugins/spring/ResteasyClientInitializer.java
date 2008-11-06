package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyClientInitializer
{
   public ResteasyClientInitializer(){
      // this initialization only needs to be done once per VM
      ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
   }
}
