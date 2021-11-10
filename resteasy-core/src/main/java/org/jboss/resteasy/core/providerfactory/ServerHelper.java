package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ReaderInterceptorRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.WriterInterceptorRegistryImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.util.Types;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ServerHelper extends CommonProviders
{
   protected boolean attachedRequestFilters;
   protected JaxrsInterceptorRegistry<ContainerRequestFilter> requestFilters;
   protected boolean attachedResponseFilters;
   protected JaxrsInterceptorRegistry<ContainerResponseFilter> responseFilters;
   protected boolean attachedAsyncResponseProviders;
   protected Map<Class<?>, AsyncResponseProvider> asyncResponseProviders;
   protected boolean attachedAsyncStreamProviders;
   protected Map<Class<?>, AsyncStreamProvider> asyncStreamProviders;
   protected boolean attachedExceptionMappers;
   protected Map<Class<?>, SortedKey<ExceptionMapper>> exceptionMappers;

   public ServerHelper() {
   }

   public ServerHelper(final ResteasyProviderFactoryImpl rpf) {
      super(rpf);
      // for a top level factory, need to allocate registries for listener registration
      requestFilters = new ContainerRequestFilterRegistryImpl(rpf);
      responseFilters = new ContainerResponseFilterRegistryImpl(rpf);
      writerInterceptorRegistry = new WriterInterceptorRegistryImpl(rpf);
      readerInterceptorRegistry = new ReaderInterceptorRegistryImpl(rpf);
   }

   // *ForWrite methods assume that there is no snapshotting required like there is for client providers
   @Override
   protected JaxrsInterceptorRegistry<ReaderInterceptor> getReaderInterceptorRegistryForWrite() {
      if (readerInterceptorRegistry == null) {
         return new ReaderInterceptorRegistryImpl(rpf);
      } else if (attachedReaderInterceptors) {
         return readerInterceptorRegistry.clone(rpf);
      }
      return readerInterceptorRegistry;
   }

   @Override
   protected JaxrsInterceptorRegistry<WriterInterceptor> getWriterInterceptorRegistryForWrite() {
      if (writerInterceptorRegistry == null) {
         return new WriterInterceptorRegistryImpl(rpf);
      } else if (attachedWriterInterceptors) {
         return writerInterceptorRegistry.clone(rpf);
      }
      return writerInterceptorRegistry;
   }

   protected MediaTypeMap<SortedKey<MessageBodyReader>> getMessageBodyReadersForWrite() {
      if (messageBodyReaders == null) {
         return new MediaTypeMap<>();
      } else if (attachedMessageBodyReaders) {
         return new MediaTypeMap<>(messageBodyReaders);
      }
      return messageBodyReaders;
   }

   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getMessageBodyWritersForWrite() {
      if (messageBodyWriters == null) {
         return new MediaTypeMap<>();
      } else if (attachedMessageBodyWriters) {
         return new MediaTypeMap<>(messageBodyWriters);
      }
      return messageBodyWriters;
   }

   public ServerHelper(final ResteasyProviderFactoryImpl rpf, final ServerHelper parent) {
      super(rpf, parent);
      if (parent.requestFilters != null) {
         attachedRequestFilters = true;
         requestFilters = parent.requestFilters;
      }
      if (parent.responseFilters != null) {
         attachedResponseFilters = true;
         responseFilters = parent.responseFilters;
      }
      if (parent.asyncResponseProviders != null) {
         attachedAsyncResponseProviders = true;
         asyncResponseProviders = parent.asyncResponseProviders;
      }
      if (parent.asyncStreamProviders != null) {
         attachedAsyncStreamProviders = true;
         asyncStreamProviders = parent.asyncStreamProviders;
      }
      if (parent.exceptionMappers != null) {
         attachedExceptionMappers = true;
         exceptionMappers = parent.exceptionMappers;
      }
   }

   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
                                           Map<Class<?>, Integer> contracts,
                                           Map<Class<?>, Integer> newContracts)
   {
      ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null && constrainedTo.value() != RuntimeType.SERVER) return;

      super.processProviderContracts(provider, priorityOverride, isBuiltin, contracts, newContracts);

      if (Utils.isA(provider, ContainerRequestFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider);
         addContainerRequestFilter(provider, priority);
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ContainerResponseFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider);
         addContainerResponseFilter(provider, priority);
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (Utils.isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider(provider);
            newContracts.put(AsyncResponseProvider.class,
                    Utils.getPriority(priorityOverride, contracts, AsyncResponseProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncResponseProvider(), e);
         }
      }
      if (Utils.isA(provider, AsyncStreamProvider.class, contracts))
      {
         try
         {
            addAsyncStreamProvider(provider);
            newContracts.put(AsyncStreamProvider.class,
                    Utils.getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
         }
      }
      if (Utils.isA(provider, ExceptionMapper.class, contracts))
      {
         try
         {
            addExceptionMapper(provider, isBuiltin);
            newContracts.put(ExceptionMapper.class,
                    Utils.getPriority(priorityOverride, contracts, ExceptionMapper.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateExceptionMapper(), e);
         }
      }
   }

   public void addExceptionMapper(Class provider, boolean isBuiltin) {
      addExceptionMapper(rpf.createProviderInstance((Class<? extends ExceptionMapper>) provider), provider,
              isBuiltin);
   }

   public void addAsyncStreamProvider(Class provider) {
      addAsyncStreamProvider(rpf.createProviderInstance((Class<? extends AsyncStreamProvider>) provider), provider);
   }

   public void addAsyncResponseProvider(Class provider) {
      AsyncResponseProvider providerInstance = rpf.createProviderInstance((Class<? extends AsyncResponseProvider>) provider);
      addAsyncResponseProvider(providerInstance,
              provider);
   }

   public void addContainerResponseFilter(Class provider, int priority) {
      JaxrsInterceptorRegistry<ContainerResponseFilter> registry = getResponseFiltersForWrite();
      registry.registerClass(provider, priority);
      attachedResponseFilters = false;
      responseFilters = registry;
   }

   public void addContainerRequestFilter(Class provider, int priority) {
      JaxrsInterceptorRegistry<ContainerRequestFilter> registry = getRequestFiltersForWrite();
      registry.registerClass(provider, priority);
      attachedRequestFilters = false;
      requestFilters = registry;
   }

   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
                                                   Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts)
   {
      ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null && constrainedTo.value() != RuntimeType.SERVER) return;

      super.processProviderInstanceContracts(provider, contracts, priorityOverride, builtIn, newContracts);

      if (Utils.isA(provider, ContainerRequestFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider.getClass());
         JaxrsInterceptorRegistry<ContainerRequestFilter> registry = getRequestFiltersForWrite();
         registry.registerSingleton((ContainerRequestFilter) provider, priority);
         attachedRequestFilters = false;
         requestFilters = registry;
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ContainerResponseFilter.class, contracts))
      {
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider.getClass());
         JaxrsInterceptorRegistry<ContainerResponseFilter> registry = getResponseFiltersForWrite();
         registry.registerSingleton((ContainerResponseFilter) provider, priority);
         attachedResponseFilters = false;
         responseFilters = registry;
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (Utils.isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider((AsyncResponseProvider) provider, provider.getClass());
            int priority = Utils.getPriority(priorityOverride, contracts, AsyncResponseProvider.class, provider.getClass());
            newContracts.put(AsyncResponseProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncResponseProvider(), e);
         }
      }
      if (Utils.isA(provider, AsyncStreamProvider.class, contracts))
      {
         try
         {
            addAsyncStreamProvider((AsyncStreamProvider) provider, provider.getClass());
            int priority = Utils.getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider.getClass());
            newContracts.put(AsyncStreamProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
         }
      }
      if (Utils.isA(provider, ExceptionMapper.class, contracts))
      {
         try
         {
            addExceptionMapper((ExceptionMapper) provider, provider.getClass(), builtIn);
            int priority = Utils.getPriority(priorityOverride, contracts, ExceptionMapper.class, provider.getClass());
            newContracts.put(ExceptionMapper.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateExceptionMapper(), e);
         }
      }
   }

   private void addAsyncResponseProvider(AsyncResponseProvider provider, Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncResponseProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      Map<Class<?>, AsyncResponseProvider> registry = getAsyncResponseProvidersForWrite();
      registry.put(asyncClass, provider);
      attachedAsyncResponseProviders = false;
      asyncResponseProviders = registry;
   }

   private void addAsyncStreamProvider(AsyncStreamProvider provider, Class providerClass)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncStreamProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      Map<Class<?>, AsyncStreamProvider> registry = getAsyncStreamProvidersForWrite();
      registry.put(asyncClass, provider);
      attachedAsyncStreamProviders = false;
      asyncStreamProviders = registry;
   }

   private void addExceptionMapper(ExceptionMapper provider, Class providerClass, boolean isBuiltin)
   {
      // Check for weld proxy.
      if (providerClass.isSynthetic())
      {
         providerClass = providerClass.getSuperclass();
      }
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(providerClass, ExceptionMapper.class)[0];

      Utils.injectProperties(rpf, providerClass, provider);

      Class<?> exceptionClass = Types.getRawType(exceptionType);
      if (!Throwable.class.isAssignableFrom(exceptionClass))
      {
         throw new RuntimeException(Messages.MESSAGES.incorrectTypeParameterExceptionMapper());
      }
       int priority = Utils.getPriority(null, null, ExceptionMapper.class, providerClass);
      SortedKey<ExceptionMapper> candidateExceptionMapper = new SortedKey<>(null, provider, providerClass, priority,
              isBuiltin);
      SortedKey<ExceptionMapper> registeredExceptionMapper;
      if (exceptionMappers != null) {
         if ((registeredExceptionMapper = exceptionMappers.get(exceptionClass)) != null
                 && (candidateExceptionMapper.compareTo(registeredExceptionMapper) > 0)) {
            return;
         }
      }
      Map<Class<?>, SortedKey<ExceptionMapper>> mappers = getExceptionMappersForWrite();
      mappers.put(exceptionClass, candidateExceptionMapper);
      attachedExceptionMappers = false;
      exceptionMappers = mappers;
   }

   protected JaxrsInterceptorRegistry<ContainerRequestFilter> getRequestFiltersForWrite() {
      if (requestFilters == null) {
         return new ContainerRequestFilterRegistryImpl(rpf);
      } else if (attachedRequestFilters) {
         return requestFilters.clone(rpf);
      }
      return requestFilters;
   }

   protected JaxrsInterceptorRegistry<ContainerResponseFilter> getResponseFiltersForWrite() {
      if (responseFilters == null) {
         return new ContainerResponseFilterRegistryImpl(rpf);
      } else if (attachedResponseFilters) {
         return responseFilters.clone(rpf);
      }
      return responseFilters;
   }

   protected Map<Class<?>, AsyncResponseProvider> getAsyncResponseProvidersForWrite() {
      if (asyncResponseProviders == null) {
         return new HashMap<>();
      } else if (lockSnapshots || attachedAsyncResponseProviders) {
         return new HashMap<>(asyncResponseProviders);
      }
      return asyncResponseProviders;
   }

   protected Map<Class<?>, AsyncStreamProvider> getAsyncStreamProvidersForWrite() {
      if (asyncStreamProviders == null) {
         return new HashMap<>();
      } else if (lockSnapshots || attachedAsyncStreamProviders) {
         return new HashMap<>(asyncStreamProviders);
      }
      return asyncStreamProviders;
   }

   protected Map<Class<?>, SortedKey<ExceptionMapper>> getExceptionMappersForWrite() {
      if (exceptionMappers == null) {
         return new HashMap<>();
      } else if (lockSnapshots || attachedExceptionMappers) {
         return new HashMap<>(exceptionMappers);
      }
      return exceptionMappers;
   }

   public JaxrsInterceptorRegistry<ContainerRequestFilter> getRequestFilters() {
      return requestFilters;
   }

   public JaxrsInterceptorRegistry<ContainerResponseFilter> getResponseFilters() {
      return responseFilters;
   }

   public Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders() {
      return asyncResponseProviders;
   }

   public Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders() {
      return asyncStreamProviders;
   }

   public Map<Class<?>, SortedKey<ExceptionMapper>> getExceptionMappers() {
      return exceptionMappers;
   }
}
