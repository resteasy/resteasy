package org.resteasy.test;

import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockDispatcherFactory
{
   public static HttpServletDispatcher createDispatcher()
   {
      HttpServletDispatcher dispatcher = new HttpServletDispatcher();
      dispatcher.setProviderFactory(new ResteasyProviderFactory());
      dispatcher.setRegistry(new Registry(dispatcher.getProviderFactory()));
      ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
      RegisterBuiltin.register(dispatcher.getProviderFactory());
      return dispatcher;
   }
}
