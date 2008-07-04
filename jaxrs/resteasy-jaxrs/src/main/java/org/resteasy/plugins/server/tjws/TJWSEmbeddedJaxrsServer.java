package org.jboss.resteasy.plugins.server.tjws;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

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
      server.setAttribute(Dispatcher.class.getName(), dispatcher);
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
