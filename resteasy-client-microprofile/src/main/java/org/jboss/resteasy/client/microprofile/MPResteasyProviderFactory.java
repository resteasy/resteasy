package org.jboss.resteasy.client.microprofile;

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
   @SuppressWarnings("rawtypes")
   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts)
   {
      super.processProviderContracts(provider, priorityOverride, isBuiltin, contracts, newContracts);
      if (isA(provider, ResponseExceptionMapper.class, contracts))
      {
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
      }
   }
   
   @SuppressWarnings("rawtypes")
   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts)
   {
      super.processProviderInstanceContracts(provider, contracts, priorityOverride, builtIn, newContracts);
      if (isA(provider, ResponseExceptionMapper.class, contracts))
      {
         if(contracts!=null) {
            Integer prio = contracts.get(ResponseExceptionMapper.class) != null ? contracts.get(ResponseExceptionMapper.class) :
                    ((ResponseExceptionMapper) provider).getPriority();
            newContracts.put(ResponseExceptionMapper.class, prio);
         } else {
            newContracts.put(ResponseExceptionMapper.class, ((ResponseExceptionMapper) provider).getPriority());
         }
      }
   }
}
