package org.resteasy.test;

import org.resteasy.Dispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockDispatcherFactory
{
   public static HttpServletDispatcher createDispatcher()
   {
      HttpServletDispatcher servlet = new HttpServletDispatcher();
      Dispatcher dispatcher = new Dispatcher(new ResteasyProviderFactory());
      servlet.setDispatcher(dispatcher);
      ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
      RegisterBuiltin.register(dispatcher.getProviderFactory());
      return servlet;
   }
}
