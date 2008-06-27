package org.resteasy.mock;

import org.resteasy.Dispatcher;
import org.resteasy.SynchronousDispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.spi.ResteasyProviderFactory;

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
