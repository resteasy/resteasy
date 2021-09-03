package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.core.interception.jaxrs.ClientRequestFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistryImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.util.Types;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientHelper extends CommonProviders
{
   protected boolean attachedRequestFilters;
   protected JaxrsInterceptorRegistry<ClientRequestFilter> requestFilters;
   protected boolean attachedResponseFilters;
   protected JaxrsInterceptorRegistry<ClientResponseFilter> responseFilters;
   protected boolean attachedAsyncClientResponseProviders;
   protected Map<Class<?>, AsyncClientResponseProvider> asyncClientResponseProviders;
   protected boolean attachedReactive;
   protected Map<Class<?>, Class<? extends RxInvokerProvider<?>>> reactiveClasses;

   public ClientHelper() {
   }

   public ClientHelper(final ResteasyProviderFactoryImpl rpf) {
      super(rpf);
   }

   /**
    * Shallow copy
    *
    * @param rpf
    * @param parent
    */
   public ClientHelper(final ResteasyProviderFactoryImpl rpf, final ClientHelper parent) {
      super(rpf, parent);
      if (parent.requestFilters != null) {
         this.requestFilters = parent.requestFilters;
         attachedRequestFilters = true;
      }
      if (parent.responseFilters != null) {
         this.responseFilters = parent.responseFilters;
         attachedResponseFilters = true;
      }
      if (parent.asyncClientResponseProviders != null) {
         this.asyncClientResponseProviders = parent.asyncClientResponseProviders;
         attachedAsyncClientResponseProviders = true;
      }
      if (parent.reactiveClasses != null) {
         this.reactiveClasses = parent.reactiveClasses;
         attachedReactive = true;
      }
   }

   protected void initializeClientProviders(ResteasyProviderFactory factory) {
      if (factory == null) return;
      if (factory.getClientRequestFilterRegistry() != null) {
         attachedRequestFilters = true;
         requestFilters = factory.getClientRequestFilterRegistry();
      }
      if (factory.getClientResponseFilters() != null) {
         attachedResponseFilters = true;
         responseFilters = factory.getClientResponseFilters();
      }
   }


   protected RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(final Class<?> clazz)
   {
      if (getReactiveClassesForWrite() == null) return null;
      Class<? extends RxInvokerProvider> rxInvokerProviderClass = getReactiveClassesForWrite().get(clazz);
      if (rxInvokerProviderClass != null)
      {
         return rpf.createProviderInstance(rxInvokerProviderClass);
      }
      return null;
   }

   protected boolean isReactive(final Class<?> clazz)
   {
      return getReactiveClassesForWrite() != null && getReactiveClassesForWrite().keySet().contains(clazz);
   }

   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
                                           Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts)
   {
      ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null && constrainedTo.value() != RuntimeType.CLIENT) return;

      super.processProviderContracts(provider, priorityOverride, isBuiltin, contracts, newContracts);

      if (Utils.isA(provider, ClientRequestFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider);
         Utils.injectProperties(rpf, provider);
         addClientRequestFilter(provider, priority);
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ClientResponseFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider);
         Utils.injectProperties(rpf, provider);
         addClientResponseFilter(provider, priority);
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (Utils.isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider(provider);
            newContracts.put(AsyncClientResponseProvider.class,
                    Utils.getPriority(priorityOverride, contracts, AsyncClientResponseProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncClientResponseProvider(), e);
         }
      }
      if (Utils.isA(provider, RxInvokerProvider.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, RxInvokerProvider.class, provider);
         newContracts.put(RxInvokerProvider.class, priority);
         addReactiveClass(provider);
      }
   }

   public void addReactiveClass(Class provider) {
      Class<?> clazz = Types.getTemplateParameterOfInterface(provider, RxInvokerProvider.class);
      clazz = Types.getTemplateParameterOfInterface(clazz, RxInvoker.class);
      if (clazz != null)
      {
         addReactiveClass(provider, clazz);
      }
   }

   public void addReactiveClass(Class provider, Class<?> clazz) {
      Map<Class<?>, Class<? extends RxInvokerProvider<?>>> registry = getReactiveClassesForWrite();
      registry.put(clazz, provider);
      attachedReactive = false;
      reactiveClasses = registry;
   }

   public void addAsyncClientResponseProvider(Class provider) {
      AsyncClientResponseProvider providerInstance = rpf.createProviderInstance((Class<? extends AsyncClientResponseProvider>) provider);
      addAsyncClientResponseProvider(
              providerInstance, provider);
   }

   public void addClientResponseFilter(Class provider, int priority) {
      JaxrsInterceptorRegistry<ClientResponseFilter> registry = getResponseFiltersForWrite();
      registry.registerClass(provider, priority);
      attachedResponseFilters = false;
      responseFilters = registry;
   }

   public void addClientRequestFilter(Class provider, int priority) {
      JaxrsInterceptorRegistry<ClientRequestFilter> registry = getRequestFiltersForWrite();
      registry.registerClass(provider, priority);
      attachedRequestFilters = false;
      requestFilters = registry;
   }

   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
                                                   Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts)
   {
      ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null && constrainedTo.value() != RuntimeType.CLIENT) return;

      super.processProviderInstanceContracts(provider, contracts, priorityOverride, builtIn, newContracts);

      if (Utils.isA(provider, ClientRequestFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider.getClass());
         JaxrsInterceptorRegistry<ClientRequestFilter> registry = getRequestFiltersForWrite();
         Utils.injectProperties(rpf, provider);
         registry.registerSingleton((ClientRequestFilter) provider, priority);
         attachedRequestFilters = false;
         requestFilters = registry;
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ClientResponseFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider.getClass());
         JaxrsInterceptorRegistry<ClientResponseFilter> registry = getResponseFiltersForWrite();
         Utils.injectProperties(rpf, provider);
         registry.registerSingleton((ClientResponseFilter) provider, priority);
         attachedResponseFilters = false;
         responseFilters = registry;
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (Utils.isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider((AsyncClientResponseProvider) provider, provider.getClass());
            int priority = Utils.getPriority(priorityOverride, contracts, AsyncClientResponseProvider.class,
                    provider.getClass());
            newContracts.put(AsyncClientResponseProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncClientResponseProvider(), e);
         }
      }
   }

   private void addAsyncClientResponseProvider(final AsyncClientResponseProvider provider, final Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncClientResponseProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);

      Class<?> asyncClass = Types.getRawType(asyncType);
      Map<Class<?>, AsyncClientResponseProvider> registry = getAsyncClientResponseProvidersForWrite();
      registry.put(asyncClass, provider);
      attachedAsyncClientResponseProviders = false;
      asyncClientResponseProviders = registry;
   }

   protected JaxrsInterceptorRegistry<ClientRequestFilter> getRequestFiltersForWrite() {
      if (requestFilters == null) {
         return new ClientRequestFilterRegistryImpl(rpf);
      } else if (lockSnapshots || attachedRequestFilters) {
         return requestFilters.clone(rpf);
      }
      return requestFilters;
   }

   protected JaxrsInterceptorRegistry<ClientResponseFilter> getResponseFiltersForWrite() {
      if (responseFilters == null) {
         return new ClientResponseFilterRegistryImpl(rpf);
      } else if (lockSnapshots || attachedRequestFilters) {
         return responseFilters.clone(rpf);
      }
      return responseFilters;
   }

   protected Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProvidersForWrite() {
      if (asyncClientResponseProviders == null) {
         return new HashMap<>();
      } else {
         if (lockSnapshots || attachedAsyncClientResponseProviders) {
            return new HashMap<>(asyncClientResponseProviders);
         }
      }
      return asyncClientResponseProviders;
   }

   protected Map<Class<?>, Class<? extends RxInvokerProvider<?>>> getReactiveClassesForWrite() {
      if (reactiveClasses == null) {
         return new HashMap<>();
      } else if (lockSnapshots || attachedReactive) {
         return new HashMap<>(reactiveClasses);
      }
      return reactiveClasses;
   }

   public JaxrsInterceptorRegistry<ClientRequestFilter> getRequestFilters() {
      return requestFilters;
   }

   public JaxrsInterceptorRegistry<ClientResponseFilter> getResponseFilters() {
      return responseFilters;
   }

   public Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders() {
      return asyncClientResponseProviders;
   }

   public Map<Class<?>, Class<? extends RxInvokerProvider<?>>> getReactiveClasses() {
      return reactiveClasses;
   }
}
