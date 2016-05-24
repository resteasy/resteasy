package org.jboss.resteasy.plugins.guice;

import com.google.inject.Binding;
import com.google.inject.Injector;

import org.jboss.resteasy.plugins.guice.i18n.LogMessages;
import org.jboss.resteasy.plugins.guice.i18n.Messages;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.Provider;

public class ModuleProcessor
{

   private final Registry registry;
   private final ResteasyProviderFactory providerFactory;

   public ModuleProcessor(final Registry registry, final ResteasyProviderFactory providerFactory)
   {
      this.registry = registry;
      this.providerFactory = providerFactory;
   }

   public void processInjector(final Injector injector)
   {
      List<Binding<?>> rootResourceBindings = new ArrayList<Binding<?>>();
      for (final Binding<?> binding : injector.getBindings().values())
      {
         final Type type = binding.getKey().getTypeLiteral().getRawType();
         if (type instanceof Class)
         {
            final Class<?> beanClass = (Class) type;
            if (GetRestful.isRootResource(beanClass))
            {
               // deferred registration
               rootResourceBindings.add(binding);
            }
            if (beanClass.isAnnotationPresent(Provider.class))
            {
               LogMessages.LOGGER.info(Messages.MESSAGES.registeringProviderInstance(beanClass.getName()));
               providerFactory.registerProviderInstance(binding.getProvider().get());
            }
         }
      }
      for (Binding<?> binding : rootResourceBindings)
      {
         Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();
         final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
         LogMessages.LOGGER.info(Messages.MESSAGES.registeringFactory(beanClass.getName()));
         registry.addResourceFactory(resourceFactory);
      }
   }
}
