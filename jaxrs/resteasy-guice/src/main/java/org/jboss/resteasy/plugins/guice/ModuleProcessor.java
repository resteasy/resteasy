package org.jboss.resteasy.plugins.guice;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

public class ModuleProcessor
{
   private final static Logger logger = LoggerFactory.getLogger(ModuleProcessor.class);

   private final Registry registry;
   private final ResteasyProviderFactory providerFactory;

   public ModuleProcessor(final Registry registry, final ResteasyProviderFactory providerFactory)
   {
      this.registry = registry;
      this.providerFactory = providerFactory;
   }

   public void process(final Module... modules)
   {
      final Injector injector = Guice.createInjector(modules);
      processInjector(injector);
   }

   public void process(final Stage stage, final Module... modules)
   {
      final Injector injector = Guice.createInjector(stage, modules);
      processInjector(injector);
   }

   public void process(final Iterable<Module> modules)
   {
      final Injector injector = Guice.createInjector(modules);
      processInjector(injector);
   }

   public void process(final Stage stage, final Iterable<Module> modules)
   {
      final Injector injector = Guice.createInjector(stage, modules);
      processInjector(injector);
   }

   private void processInjector(final Injector injector)
   {
      for (final Binding<?> binding : injector.getBindings().values())
      {
         final Type type = binding.getKey().getTypeLiteral().getType();
         if (type instanceof Class)
         {
            final Class<?> beanClass = (Class) type;
            if (GetRestful.isRootResource(beanClass))
            {
               final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
               logger.info("registering factory for {}", beanClass);
               registry.addResourceFactory(resourceFactory);
            }
            if (beanClass.isAnnotationPresent(Provider.class))
            {
               logger.info("registering provider instance for {}", beanClass);
               providerFactory.registerProviderInstance(binding.getProvider().get());
            }
         }
      }
   }
}
