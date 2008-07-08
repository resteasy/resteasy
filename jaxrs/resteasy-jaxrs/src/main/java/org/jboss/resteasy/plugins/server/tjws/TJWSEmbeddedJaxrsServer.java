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
   protected ResteasyProviderFactory factory;
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
   }

   @Override
   public void start()
   {
      server.setAttribute(ResteasyProviderFactory.class.getName(), getFactory());
      server.setAttribute(Registry.class.getName(), getRegistry());
      server.setAttribute(Dispatcher.class.getName(), getDispatcher());
      addServlet(rootResourcePath, servlet);
      servlet.setContextPath(rootResourcePath);
      super.start();
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public void setFactory(ResteasyProviderFactory factory)
   {
      this.factory = factory;
   }

   public ResteasyProviderFactory getFactory()
   {
      if (factory == null)
      {
         factory = new ResteasyProviderFactory();
         ResteasyProviderFactory.setInstance(factory);
         RegisterBuiltin.register(factory);
      }
      return factory;
   }

   public Registry getRegistry()
   {
      return getDispatcher().getRegistry();
   }

   public Dispatcher getDispatcher()
   {
      if (dispatcher == null)
      {
         dispatcher = new SynchronousDispatcher();
         dispatcher.setProviderFactory(getFactory());
         registry = dispatcher.getRegistry();
      }
      return dispatcher;
   }

   public void setSecurityDomain(SecurityDomain sc)
   {
      servlet.setSecurityDomain(sc);
   }

   public void addApplicationConfig(ApplicationConfig config)
   {
      getDispatcher().setLanguageMappings(config.getLanguageMappings());
      getDispatcher().setMediaTypeMappings(config.getMediaTypeMappings());
      if (config.getResourceClasses() != null)
         for (Class clazz : config.getResourceClasses()) getRegistry().addPerRequestResource(clazz);
      if (config.getProviderClasses() != null)
      {
         for (Class provider : config.getProviderClasses())
         {
            factory.registerProvider(provider);
         }
      }
   }
}
