package org.jboss.resteasy.core.providerfactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.interception.jaxrs.ClientRequestFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ReaderInterceptorRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.WriterInterceptorRegistryImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.util.Types;

/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientHelper
{
   private final ResteasyProviderFactoryImpl rpf;
   private MediaTypeMap<SortedKey<MessageBodyReader>> clientMessageBodyReaders;
   private MediaTypeMap<SortedKey<MessageBodyWriter>> clientMessageBodyWriters;
   private JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry;
   private JaxrsInterceptorRegistry<ClientResponseFilter> clientResponseFilters;
   private JaxrsInterceptorRegistry<ReaderInterceptor> clientReaderInterceptorRegistry;
   private JaxrsInterceptorRegistry<WriterInterceptor> clientWriterInterceptorRegistry;
   private Set<DynamicFeature> clientDynamicFeatures;
   private Map<Class<?>, AsyncClientResponseProvider> asyncClientResponseProviders;
   private Map<Class<?>, Class<? extends RxInvokerProvider<?>>> reactiveClasses;

   public ClientHelper(final ResteasyProviderFactoryImpl rpf)
   {
      this.rpf = rpf;
   }

   protected void initializeDefault()
   {
      reactiveClasses = new ConcurrentHashMap<>();
   }

   protected void initialize(ResteasyProviderFactoryImpl parent)
   {
      clientMessageBodyReaders = parent == null ? new MediaTypeMap<>() : parent.getClientMessageBodyReaders().clone();
      clientMessageBodyWriters = parent == null ? new MediaTypeMap<>() : parent.getClientMessageBodyWriters().clone();
      clientRequestFilterRegistry = parent == null ? new ClientRequestFilterRegistryImpl(rpf) : parent.getClientRequestFilterRegistry().clone(rpf);
      clientResponseFilters = parent == null ? new ClientResponseFilterRegistryImpl(rpf) : parent.getClientResponseFilters().clone(rpf);
      clientReaderInterceptorRegistry = parent == null ? new ReaderInterceptorRegistryImpl(rpf) : parent.getClientReaderInterceptorRegistry().clone(rpf);
      clientWriterInterceptorRegistry = parent == null ? new WriterInterceptorRegistryImpl(rpf) : parent.getClientWriterInterceptorRegistry().clone(rpf);

      clientDynamicFeatures = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getClientDynamicFeatures());
      asyncClientResponseProviders = parent == null ? new ConcurrentHashMap<>(1) : new ConcurrentHashMap<>(parent.getAsyncClientResponseProviders());
      reactiveClasses = parent == null ? new ConcurrentHashMap<>(8) : new ConcurrentHashMap<>(parent.clientHelper.reactiveClasses);
   }

   protected void initializeClientProviders(ResteasyProviderFactory factory)
   {
      clientRequestFilterRegistry = factory == null ? new ClientRequestFilterRegistryImpl(rpf) : factory.getClientRequestFilterRegistry().clone(rpf);
      clientResponseFilters =  factory == null ? new ClientResponseFilterRegistryImpl(rpf) : factory.getClientResponseFilters().clone(rpf);
   }

   protected JaxrsInterceptorRegistry<ReaderInterceptor> getClientReaderInterceptorRegistry(ResteasyProviderFactory parent)
   {
      if (clientReaderInterceptorRegistry == null && parent != null)
         return parent.getClientReaderInterceptorRegistry();
      return clientReaderInterceptorRegistry;
   }

   protected JaxrsInterceptorRegistry<WriterInterceptor> getClientWriterInterceptorRegistry(ResteasyProviderFactory parent)
   {
      if (clientWriterInterceptorRegistry == null && parent != null)
         return parent.getClientWriterInterceptorRegistry();
      return clientWriterInterceptorRegistry;
   }

   protected JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilterRegistry(ResteasyProviderFactory parent)
   {
      if (clientRequestFilterRegistry == null && parent != null)
         return parent.getClientRequestFilterRegistry();
      return clientRequestFilterRegistry;
   }

   protected JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters(ResteasyProviderFactory parent)
   {
      if (clientResponseFilters == null && parent != null)
         return parent.getClientResponseFilters();
      return clientResponseFilters;
   }

   protected Set<DynamicFeature> getClientDynamicFeatures(ResteasyProviderFactory parent)
   {
      if (clientDynamicFeatures == null && parent != null)
         return parent.getClientDynamicFeatures();
      return clientDynamicFeatures;
   }

   protected Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders(ResteasyProviderFactory parent)
   {
      if (asyncClientResponseProviders == null && parent != null)
         return parent.getAsyncClientResponseProviders();
      return asyncClientResponseProviders;
   }

   protected RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz)
   {
      Class<? extends RxInvokerProvider> rxInvokerProviderClass = reactiveClasses.get(clazz);
      if (rxInvokerProviderClass != null)
      {
         return rpf.createProviderInstance(rxInvokerProviderClass);
      }
      return null;
   }

   protected boolean isReactive(Class<?> clazz)
   {
      return reactiveClasses.keySet().contains(clazz);
   }

   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      if (Utils.isA(provider, MessageBodyReader.class, contracts))
      {
         try
         {
            int priority = Utils.getPriority(priorityOverride, contracts, MessageBodyReader.class, provider);
            addMessageBodyReader(Utils.createProviderInstance(rpf, (Class<? extends MessageBodyReader>) provider), provider,
                  priority, isBuiltin, parent);
            newContracts.put(MessageBodyReader.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyReader(), e);
         }
      }
      if (Utils.isA(provider, MessageBodyWriter.class, contracts))
      {
         try
         {
            int priority = Utils.getPriority(priorityOverride, contracts, MessageBodyWriter.class, provider);
            addMessageBodyWriter(Utils.createProviderInstance(rpf, (Class<? extends MessageBodyWriter>) provider), provider,
                  priority, isBuiltin, parent);
            newContracts.put(MessageBodyWriter.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyWriter(), e);
         }
      }
      if (Utils.isA(provider, ClientRequestFilter.class, contracts))
      {
         if (clientRequestFilterRegistry == null)
         {
            clientRequestFilterRegistry = parent.getClientRequestFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider);
         clientRequestFilterRegistry.registerClass(provider, priority);
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ClientResponseFilter.class, contracts))
      {
         if (clientResponseFilters == null)
         {
            clientResponseFilters = parent.getClientResponseFilters().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider);
         clientResponseFilters.registerClass(provider, priority);
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (Utils.isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(rpf);
            }
            clientReaderInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(rpf);
            }
            clientReaderInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (Utils.isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, WriterInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(rpf);
            }
            clientWriterInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(rpf);
            }
            clientWriterInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (Utils.isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, DynamicFeature.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) rpf.injectedInstance(provider));
         }
         if (constrainedTo == null)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) rpf.injectedInstance(provider));
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (Utils.isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider(
                  rpf.createProviderInstance((Class<? extends AsyncClientResponseProvider>) provider), provider, parent);
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
         Class<?> clazz = Types.getTemplateParameterOfInterface(provider, RxInvokerProvider.class);
         clazz = Types.getTemplateParameterOfInterface(clazz, RxInvoker.class);
         if (clazz != null)
         {
            reactiveClasses.put(clazz, provider);
         }
      }
   }

   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      if (Utils.isA(provider, MessageBodyReader.class, contracts))
      {
         try
         {
            int priority = Utils.getPriority(priorityOverride, contracts, MessageBodyReader.class, provider.getClass());
            addMessageBodyReader((MessageBodyReader) provider, provider.getClass(), priority, builtIn, parent);
            newContracts.put(MessageBodyReader.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyReader(), e);
         }
      }
      if (Utils.isA(provider, MessageBodyWriter.class, contracts))
      {
         try
         {
            int priority = Utils.getPriority(priorityOverride, contracts, MessageBodyWriter.class, provider.getClass());
            addMessageBodyWriter((MessageBodyWriter) provider, provider.getClass(), priority, builtIn, parent);
            newContracts.put(MessageBodyWriter.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateMessageBodyWriter(), e);
         }
      }
      if (Utils.isA(provider, ClientRequestFilter.class, contracts))
      {
         if (clientRequestFilterRegistry == null)
         {
            clientRequestFilterRegistry = parent.getClientRequestFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ClientRequestFilter.class, provider.getClass());
         clientRequestFilterRegistry.registerSingleton((ClientRequestFilter) provider, priority);
         newContracts.put(ClientRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ClientResponseFilter.class, contracts))
      {
         if (clientResponseFilters == null)
         {
            clientResponseFilters = parent.getClientResponseFilters().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ClientResponseFilter.class, provider.getClass());
         clientResponseFilters.registerSingleton((ClientResponseFilter) provider, priority);
         newContracts.put(ClientResponseFilter.class, priority);
      }
      if (Utils.isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(rpf);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = parent.getClientReaderInterceptorRegistry().clone(rpf);
            }
            clientReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (Utils.isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, WriterInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(rpf);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = parent.getClientWriterInterceptorRegistry().clone(rpf);
            }
            clientWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (Utils.isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, DynamicFeature.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.CLIENT)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) provider);
         }
         if (constrainedTo == null)
         {
            if (clientDynamicFeatures == null)
            {
               clientDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getClientDynamicFeatures());
            }
            clientDynamicFeatures.add((DynamicFeature) provider);
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (Utils.isA(provider, AsyncClientResponseProvider.class, contracts))
      {
         try
         {
            addAsyncClientResponseProvider((AsyncClientResponseProvider) provider, provider.getClass(), parent);
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

   protected MediaTypeMap<SortedKey<MessageBodyReader>> getClientMessageBodyReaders(ResteasyProviderFactoryImpl parent)
   {
      if (clientMessageBodyReaders == null && parent != null)
         return parent.getClientMessageBodyReaders();
      return clientMessageBodyReaders;
   }

   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getClientMessageBodyWriters(ResteasyProviderFactoryImpl parent)
   {
      if (clientMessageBodyWriters == null && parent != null)
         return parent.getClientMessageBodyWriters();
      return clientMessageBodyWriters;
   }

   protected void addMessageBodyReader(MessageBodyReader provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      SortedKey<MessageBodyReader> key = new SortedKey<MessageBodyReader>(MessageBodyReader.class, provider,
            providerClass, priority, isBuiltin);
      Utils.injectProperties(rpf, providerClass, provider);
      Consumes consumeMime = provider.getClass().getAnnotation(Consumes.class);
      RuntimeType type = null;
      ConstrainedTo constrainedTo = providerClass.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null)
         type = constrainedTo.value();

      if ((type == null || type == RuntimeType.CLIENT) && clientMessageBodyReaders == null)
      {
         clientMessageBodyReaders = parent.getClientMessageBodyReaders().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            if (type == null)
            {
               clientMessageBodyReaders.add(MediaType.valueOf(consume), key);
            }
            else if (type == RuntimeType.CLIENT)
            {
               clientMessageBodyReaders.add(MediaType.valueOf(consume), key);
            }
         }
      }
      else
      {
         if (type == null)
         {
            clientMessageBodyReaders.add(new MediaType("*", "*"), key);
         }
         else if (type == RuntimeType.CLIENT)
         {
            clientMessageBodyReaders.add(new MediaType("*", "*"), key);
         }
      }
   }

   protected void addMessageBodyWriter(MessageBodyWriter provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      Utils.injectProperties(rpf, providerClass, provider);
      Produces consumeMime = provider.getClass().getAnnotation(Produces.class);
      SortedKey<MessageBodyWriter> key = new SortedKey<MessageBodyWriter>(MessageBodyWriter.class, provider,
            providerClass, priority, isBuiltin);
      RuntimeType type = null;
      ConstrainedTo constrainedTo = providerClass.getAnnotation(ConstrainedTo.class);
      if (constrainedTo != null)
         type = constrainedTo.value();

      if ((type == null || type == RuntimeType.CLIENT) && clientMessageBodyWriters == null)
      {
         clientMessageBodyWriters = parent.getClientMessageBodyWriters().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: " + mime);
            if (type == null)
            {
               clientMessageBodyWriters.add(MediaType.valueOf(consume), key);
            }
            else if (type == RuntimeType.CLIENT)
            {
               clientMessageBodyWriters.add(MediaType.valueOf(consume), key);
            }
         }
      }
      else
      {
         //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: default */*");
         if (type == null)
         {
            clientMessageBodyWriters.add(new MediaType("*", "*"), key);
         }
         else if (type == RuntimeType.CLIENT)
         {
            clientMessageBodyWriters.add(new MediaType("*", "*"), key);
         }
      }
   }

   private void addAsyncClientResponseProvider(AsyncClientResponseProvider provider, Class providerClass, ResteasyProviderFactory parent)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncClientResponseProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);

      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncClientResponseProviders == null)
      {
         asyncClientResponseProviders = new ConcurrentHashMap<Class<?>, AsyncClientResponseProvider>();
         asyncClientResponseProviders.putAll(parent.getAsyncClientResponseProviders());
      }
      asyncClientResponseProviders.put(asyncClass, provider);
   }

}
