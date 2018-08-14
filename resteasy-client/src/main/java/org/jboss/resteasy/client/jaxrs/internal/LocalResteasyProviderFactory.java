package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.RuntimeType;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * A version of ResteasyProviderFactory which does not reference its parent
 * after it is created. Used for client framework Configurables.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * <p>
 * Date April 27, 2016
 */
public class LocalResteasyProviderFactory extends ResteasyProviderFactory
{
   
   public LocalResteasyProviderFactory(ResteasyProviderFactory factory)
   {
      super(factory, true);
   }

   @Override
   public RuntimeType getRuntimeType()
   {
      return RuntimeType.CLIENT;
   }
}
