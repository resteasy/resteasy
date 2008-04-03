package org.resteasy.plugins.server.tjws;

import org.resteasy.Dispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.EmbeddedJaxrsServer;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSEmbeddedJaxrsServer extends TJWSServletServer implements EmbeddedJaxrsServer
{
   protected ResteasyProviderFactory factory = new ResteasyProviderFactory();
   protected Registry registry;
   protected Dispatcher dispatcher;
   protected HttpServletDispatcher servlet = new HttpServletDispatcher();

   protected String rootResourcePath = "";

   public void setRootResourcePath(String rootResourcePath)
   {
      this.rootResourcePath = rootResourcePath;
   }

   public TJWSEmbeddedJaxrsServer()
   {
      ResteasyProviderFactory.setInstance(factory);

      dispatcher = new Dispatcher(factory);
      registry = dispatcher.getRegistry();
      ResteasyProviderFactory.setInstance(factory);
      RegisterBuiltin.register(factory);
   }

   @Override
   public void start()
   {
      server.setAttribute(ResteasyProviderFactory.class.getName(), factory);
      server.setAttribute(Registry.class.getName(), registry);
      server.setAttribute(Dispatcher.class.getName(), dispatcher);
      addServlet(rootResourcePath, servlet);
      super.start();
   }

   public ResteasyProviderFactory getFactory()
   {
      return factory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }
}
