package org.resteasy.plugins.server.tjws;

import org.resteasy.Dispatcher;
import org.resteasy.SynchronousDispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.resteasy.plugins.server.embedded.SecurityDomain;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.ApplicationConfig;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSEmbeddedJaxrsServer extends TJWSServletServer implements EmbeddedJaxrsServer
{
   protected ResteasyProviderFactory factory = new ResteasyProviderFactory();
   protected Registry registry;
   protected Dispatcher dispatcher;
   protected TJWSServletDispatcher servlet = new TJWSServletDispatcher();

   protected String rootResourcePath = "";

   public void setRootResourcePath(String rootResourcePath)
   {
      this.rootResourcePath = rootResourcePath;
   }

   public TJWSEmbeddedJaxrsServer()
   {
      ResteasyProviderFactory.setInstance(factory);

      dispatcher = new SynchronousDispatcher(factory);
      registry = dispatcher.getRegistry();
      ResteasyProviderFactory.setInstance(factory);
      RegisterBuiltin.register(factory);
   }

   @Override
   public void start()
   {
      server.setAttribute(ResteasyProviderFactory.class.getName(), factory);
      server.setAttribute(Registry.class.getName(), registry);
      server.setAttribute(SynchronousDispatcher.class.getName(), dispatcher);
      addServlet(rootResourcePath, servlet);
      servlet.setContextPath(rootResourcePath);
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

   public void setSecurityDomain(SecurityDomain sc)
   {
      servlet.setSecurityDomain(sc);
   }

   public void addApplicationConfig(ApplicationConfig config)
   {
      dispatcher.setLanguageMappings(config.getLanguageMappings());
      dispatcher.setMediaTypeMappings(config.getMediaTypeMappings());
      if (config.getResourceClasses() != null)
         for (Class clazz : config.getResourceClasses()) registry.addPerRequestResource(clazz);
      if (config.getProviderClasses() != null)
      {
         for (Class provider : config.getProviderClasses())
         {
            factory.registerProvider(provider);
         }
      }
   }
}
