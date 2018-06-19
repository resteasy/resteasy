package org.jboss.resteasy.client.microprofile;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * An extension of ResteasyProviderFactory for implementing MP REST Client
 * 
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 *
 */
public class MPResteasyProviderFactory extends ResteasyProviderFactory
{
   @Override
   public void registerProvider(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts)
   {
      super.registerProvider(provider, priorityOverride, isBuiltin, contracts);
      if (isA(provider, ResponseExceptionMapper.class, contracts))
      {
         Map<Class<?>, Integer> newContracts = getClassContracts().get(provider);
         if (newContracts == null) {
            newContracts = new HashMap<Class<?>, Integer>();
         }
         try {
            Object mapper = provider.newInstance();
            registerProviderInstance(mapper, contracts, null, false);

            if(contracts!=null) {
               Integer prio = contracts.get(ResponseExceptionMapper.class) != null ? contracts.get(ResponseExceptionMapper.class) :
                       ((ResponseExceptionMapper) mapper).getPriority();

               newContracts.put(ResponseExceptionMapper.class, prio);
            } else {
               newContracts.put(ResponseExceptionMapper.class, ((ResponseExceptionMapper) mapper).getPriority());
            }
         } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register provider", e);
         }
         providerClasses.add(provider);
         getClassContracts().put(provider, newContracts);
      }
   }
   
   @Override
   public void registerProviderInstance(Object provider, Map<Class<?>, Integer> contracts, Integer priorityOverride, boolean builtIn)
   {
      super.registerProviderInstance(provider, contracts, priorityOverride, builtIn);
      if (isA(provider, ResponseExceptionMapper.class, contracts))
      {
         Map<Class<?>, Integer> newContracts = getClassContracts().get(provider.getClass());
         if (newContracts == null) {
            newContracts = new HashMap<Class<?>, Integer>();
         }
         if(contracts!=null) {
            Integer prio = contracts.get(ResponseExceptionMapper.class) != null ? contracts.get(ResponseExceptionMapper.class) :
                    ((ResponseExceptionMapper) provider).getPriority();
            newContracts.put(ResponseExceptionMapper.class, prio);
         } else {
            newContracts.put(ResponseExceptionMapper.class, ((ResponseExceptionMapper) provider).getPriority());
         }
         providerInstances.add(provider);
         getClassContracts().put(provider.getClass(), newContracts);
      }
   }
}
