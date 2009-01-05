package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class ResteasyClientInitializer
{
   public ResteasyClientInitializer()
   {
      // this initialization only needs to be done once per VM
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
   }
}
