package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactoryBuilder;

public class ResteasyProviderFactoryBuilderImpl implements ResteasyProviderFactoryBuilder
{
   @Override
   public ResteasyProviderFactory newInstance(boolean registerBuiltin)
   {
      ResteasyProviderFactoryImpl rpf = new ResteasyProviderFactoryImpl();
      if (registerBuiltin) {
         rpf.registerBuiltin();
      }
      return rpf;
   }
}
