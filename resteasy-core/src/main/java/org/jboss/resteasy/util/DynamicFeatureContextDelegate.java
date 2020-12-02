package org.jboss.resteasy.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 *
 * @author Nicolas NESMON
 *
 */
public class DynamicFeatureContextDelegate extends FeatureContextDelegate
{

   public DynamicFeatureContextDelegate(final Configurable<?> configurable)
   {
      super(configurable);
   }

   @Override
   public FeatureContext property(String name, Object value)
   {
      super.property(name, value);
      return this;
   }

   @Override
   public FeatureContext register(Class<?> componentClass)
   {
      if (checkRegistrability(componentClass, null))
      {
         super.register(componentClass);
      }
      return this;
   }

   @Override
   public FeatureContext register(Class<?> componentClass, int priority)
   {
      if (checkRegistrability(componentClass, null))
      {
         super.register(componentClass, priority);
      }
      return this;
   }

   @Override
   public FeatureContext register(Class<?> componentClass, Class<?>... contracts)
   {
      Set<Class<?>> contractsSet = new HashSet<>();
      Collections.addAll(contractsSet, contracts);
      if (checkRegistrability(componentClass, contractsSet))
      {
         super.register(componentClass, contracts);
      }
      return this;
   }

   @Override
   public FeatureContext register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      if (checkRegistrability(componentClass, contracts == null ? null : contracts.keySet()))
      {
         super.register(componentClass, contracts);
      }
      return this;
   }

   @Override
   public FeatureContext register(Object component)
   {
      if (checkRegistrability(component, null))
      {
         super.register(component);
      }
      return this;
   }

   @Override
   public FeatureContext register(Object component, int priority)
   {
      if (checkRegistrability(component, null))
      {
         super.register(component, priority);
      }
      return this;
   }

   @Override
   public FeatureContext register(Object component, Class<?>... contracts)
   {
      Set<Class<?>> contractsSet = new HashSet<>();
      Collections.addAll(contractsSet, contracts);
      if (checkRegistrability(component, contractsSet))
      {
         super.register(component, contracts);
      }
      return this;
   }

   @Override
   public FeatureContext register(Object component, Map<Class<?>, Integer> contracts)
   {
      if (checkRegistrability(component, contracts == null ? null : contracts.keySet()))
      {
         super.register(component, contracts);
      }
      return this;
   }

   // Test if a component class is being registrable
   private boolean checkRegistrability(Class<?> componentClass, Set<Class<?>> contracts)
   {
      if (ResteasyProviderFactory.isA(componentClass, ContextResolver.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(componentClass, ContextResolver.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(componentClass, ExceptionMapper.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(componentClass, ExceptionMapper.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(componentClass, MessageBodyWriter.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(componentClass, MessageBodyWriter.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(componentClass, MessageBodyReader.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(componentClass, MessageBodyReader.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(componentClass, DynamicFeature.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(componentClass, DynamicFeature.class);
         return false;
      }
      return true;
   }

   // Test if a component is being registrable
   private boolean checkRegistrability(Object component, Set<Class<?>> contracts)
   {
      if (ResteasyProviderFactory.isA(component, ContextResolver.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(component.getClass(), ContextResolver.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(component, ExceptionMapper.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(component.getClass(), ExceptionMapper.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(component, MessageBodyWriter.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(component.getClass(), MessageBodyWriter.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(component, MessageBodyReader.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(component.getClass(), MessageBodyReader.class);
         return false;
      }
      else if (ResteasyProviderFactory.isA(component, DynamicFeature.class, contracts))
      {
         LogMessages.LOGGER.providerCantBeDynamicallyBoundToMethod(component.getClass(), DynamicFeature.class);
         return false;
      }
      return true;
   }

}
