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
      dispatcher.getDispatcher().setProviderFactory(new ResteasyProviderFactory());
      dispatcher.getDispatcher().setRegistry(new Registry(dispatcher.getDispatcher().getProviderFactory()));
      ResteasyProviderFactory.setInstance(dispatcher.getDispatcher().getProviderFactory());
      RegisterBuiltin.register(dispatcher.getDispatcher().getProviderFactory());
      return dispatcher;
   }
}
