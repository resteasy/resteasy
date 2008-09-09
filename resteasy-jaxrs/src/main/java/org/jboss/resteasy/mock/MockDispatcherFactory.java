package org.jboss.resteasy.mock;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Creates a mock Dispatcher that you can invoke on locally
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockDispatcherFactory
{

   public static Dispatcher createDispatcher()
   {
      Dispatcher dispatcher = new SynchronousDispatcher(new ResteasyProviderFactory());
      ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
      RegisterBuiltin.register(dispatcher.getProviderFactory());
      return dispatcher;
   }
}
