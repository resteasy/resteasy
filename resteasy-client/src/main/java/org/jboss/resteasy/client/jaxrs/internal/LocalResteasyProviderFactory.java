package org.jboss.resteasy.client.jaxrs.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * A version of ResteasyProviderFactory which does not reference its parent
 * after it is created. Used for client framework Configurables.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * <p>
 * Date April 27, 2016
 */
public class LocalResteasyProviderFactory extends ResteasyProviderFactory
{
   
   public LocalResteasyProviderFactory(ResteasyProviderFactory factory)
   {
      super(factory, true);
   }
   
   @Override
   public boolean isEnabled(Feature feature)
   {
      for (Feature f : enabledFeatures)
      {
         if (f == feature)
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isEnabled(Class<? extends Feature> featureClass)
   {
      if (enabledFeatures == null) return false;
      for (Feature feature : enabledFeatures)
      {
         if (featureClass.equals(feature.getClass()))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isRegistered(Class<?> componentClass)
   {
      if (providerClasses.contains(componentClass)) return true;
      for (Object obj : providerInstances)
      {
         if (obj.getClass().equals(componentClass)) return true;
      }
      return false;
   }
   
   @Override
   public Map<Class<?>, Integer> getContracts(Class<?> componentClass)
   {
      Map<Class<?>, Integer> classIntegerMap = classContracts.get(componentClass);
      if (classIntegerMap == null) return Collections.emptyMap();
      return classIntegerMap;
   }

   @Override
   public Set<Class<?>> getProviderClasses()
   {
      return new HashSet<Class<?>>(providerClasses);
   }
   
   @Override
   public Set<Object> getProviderInstances()
   {
      return new HashSet<Object>(providerInstances);
   }
   
   @Override
   public RuntimeType getRuntimeType()
   {
      return RuntimeType.CLIENT;
   }
}
