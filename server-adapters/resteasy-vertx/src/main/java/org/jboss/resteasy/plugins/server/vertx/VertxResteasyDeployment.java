package org.jboss.resteasy.plugins.server.vertx;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.spi.Registry;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VertxResteasyDeployment extends ResteasyDeploymentImpl
{

   @Override
   public VertxRegistry getRegistry()
   {
      Registry registry = super.getRegistry();
      if (!(registry instanceof VertxRegistry))
      {
         registry = new VertxRegistry(registry, getProviderFactory().getResourceBuilder());
      }
      return (VertxRegistry) registry;
   }

   @Override
   public void setRegistry(Registry registry)
   {
      super.setRegistry(registry);
   }
}
