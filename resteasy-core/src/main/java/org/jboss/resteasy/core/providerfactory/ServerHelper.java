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
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ReaderInterceptorRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.WriterInterceptorRegistryImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.util.Types;

/**
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ServerHelper
{
   private final ResteasyProviderFactoryImpl rpf;
   private MediaTypeMap<SortedKey<MessageBodyReader>> serverMessageBodyReaders;
   private MediaTypeMap<SortedKey<MessageBodyWriter>> serverMessageBodyWriters;
   private JaxrsInterceptorRegistry<ContainerRequestFilter> containerRequestFilterRegistry;
   private JaxrsInterceptorRegistry<ContainerResponseFilter> containerResponseFilterRegistry;
   private JaxrsInterceptorRegistry<ReaderInterceptor> serverReaderInterceptorRegistry;
   private JaxrsInterceptorRegistry<WriterInterceptor> serverWriterInterceptorRegistry;
   private Set<DynamicFeature> serverDynamicFeatures;
   private Map<Class<?>, AsyncResponseProvider> asyncResponseProviders;
   private Map<Class<?>, AsyncStreamProvider> asyncStreamProviders;

   public ServerHelper(final ResteasyProviderFactoryImpl rpf)
   {
      this.rpf = rpf;
   }

   protected void initialize(ResteasyProviderFactoryImpl parent)
   {
      serverDynamicFeatures = parent == null ? new CopyOnWriteArraySet<>() : new CopyOnWriteArraySet<>(parent.getServerDynamicFeatures());
      asyncResponseProviders = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncResponseProviders());
      asyncStreamProviders = parent == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(parent.getAsyncStreamProviders());

      serverMessageBodyReaders = parent == null ? new MediaTypeMap<>() : parent.getServerMessageBodyReaders().clone();
      serverMessageBodyWriters = parent == null ? new MediaTypeMap<>() : parent.getServerMessageBodyWriters().clone();
      containerRequestFilterRegistry = parent == null ? new ContainerRequestFilterRegistryImpl(rpf) : parent.getContainerRequestFilterRegistry().clone(rpf);
      containerResponseFilterRegistry = parent == null ? new ContainerResponseFilterRegistryImpl(rpf) : parent.getContainerResponseFilterRegistry().clone(rpf);
      serverReaderInterceptorRegistry = parent == null ? new ReaderInterceptorRegistryImpl(rpf) : parent.getServerReaderInterceptorRegistry().clone(rpf);
      serverWriterInterceptorRegistry = parent == null ? new WriterInterceptorRegistryImpl(rpf) : parent.getServerWriterInterceptorRegistry().clone(rpf);
   }

   protected JaxrsInterceptorRegistry<ReaderInterceptor> getServerReaderInterceptorRegistry(ResteasyProviderFactory parent)
   {
      if (serverReaderInterceptorRegistry == null && parent != null)
         return parent.getServerReaderInterceptorRegistry();
      return serverReaderInterceptorRegistry;
   }

   protected JaxrsInterceptorRegistry<WriterInterceptor> getServerWriterInterceptorRegistry(ResteasyProviderFactory parent)
   {
      if (serverWriterInterceptorRegistry == null && parent != null)
         return parent.getServerWriterInterceptorRegistry();
      return serverWriterInterceptorRegistry;
   }

   protected JaxrsInterceptorRegistry<ContainerRequestFilter> getContainerRequestFilterRegistry(ResteasyProviderFactory parent)
   {
      if (containerRequestFilterRegistry == null && parent != null)
         return parent.getContainerRequestFilterRegistry();
      return containerRequestFilterRegistry;
   }

   protected JaxrsInterceptorRegistry<ContainerResponseFilter> getContainerResponseFilterRegistry(ResteasyProviderFactory parent)
   {
      if (containerResponseFilterRegistry == null && parent != null)
         return parent.getContainerResponseFilterRegistry();
      return containerResponseFilterRegistry;
   }

   protected Set<DynamicFeature> getServerDynamicFeatures(ResteasyProviderFactory parent)
   {
      if (serverDynamicFeatures == null && parent != null)
         return parent.getServerDynamicFeatures();
      return serverDynamicFeatures;
   }

   protected Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders(ResteasyProviderFactory parent)
   {
      if (asyncResponseProviders == null && parent != null)
         return parent.getAsyncResponseProviders();
      return asyncResponseProviders;
   }

   protected Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders(ResteasyProviderFactory parent)
   {
      if (asyncStreamProviders == null && parent != null)
         return parent.getAsyncStreamProviders();
      return asyncStreamProviders;
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
      if (Utils.isA(provider, ContainerRequestFilter.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider);
         containerRequestFilterRegistry.registerClass(provider, priority);
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ContainerResponseFilter.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider);
         containerResponseFilterRegistry.registerClass(provider, priority);
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (Utils.isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(rpf);
            }
            serverReaderInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(rpf);
            }
            serverReaderInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (Utils.isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, WriterInterceptor.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(rpf);
            }
            serverWriterInterceptorRegistry.registerClass(provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(rpf);
            }
            serverWriterInterceptorRegistry.registerClass(provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (Utils.isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, DynamicFeature.class, provider);
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) rpf.injectedInstance(provider));
         }
         if (constrainedTo == null)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) rpf.injectedInstance(provider));
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (Utils.isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider(rpf.createProviderInstance((Class<? extends AsyncResponseProvider>) provider),
                  provider, parent);
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
            addAsyncStreamProvider(rpf.createProviderInstance((Class<? extends AsyncStreamProvider>) provider), provider, parent);
            newContracts.put(AsyncStreamProvider.class,
                  Utils.getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
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
      if (Utils.isA(provider, ContainerRequestFilter.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = parent.getContainerRequestFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerRequestFilter.class, provider.getClass());
         containerRequestFilterRegistry.registerSingleton((ContainerRequestFilter) provider, priority);
         newContracts.put(ContainerRequestFilter.class, priority);
      }
      if (Utils.isA(provider, ContainerResponseFilter.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = parent.getContainerResponseFilterRegistry().clone(rpf);
         }
         int priority = Utils.getPriority(priorityOverride, contracts, ContainerResponseFilter.class, provider.getClass());
         containerResponseFilterRegistry.registerSingleton((ContainerResponseFilter) provider, priority);
         newContracts.put(ContainerResponseFilter.class, priority);
      }
      if (Utils.isA(provider, ReaderInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, ReaderInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(rpf);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = parent.getServerReaderInterceptorRegistry().clone(rpf);
            }
            serverReaderInterceptorRegistry.registerSingleton((ReaderInterceptor) provider, priority);
         }
         newContracts.put(ReaderInterceptor.class, priority);
      }
      if (Utils.isA(provider, WriterInterceptor.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, WriterInterceptor.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(rpf);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         if (constrainedTo == null)
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = parent.getServerWriterInterceptorRegistry().clone(rpf);
            }
            serverWriterInterceptorRegistry.registerSingleton((WriterInterceptor) provider, priority);
         }
         newContracts.put(WriterInterceptor.class, priority);
      }
      if (Utils.isA(provider, DynamicFeature.class, contracts))
      {
         ConstrainedTo constrainedTo = (ConstrainedTo) provider.getClass().getAnnotation(ConstrainedTo.class);
         int priority = Utils.getPriority(priorityOverride, contracts, DynamicFeature.class, provider.getClass());
         if (constrainedTo != null && constrainedTo.value() == RuntimeType.SERVER)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) provider);
         }
         if (constrainedTo == null)
         {
            if (serverDynamicFeatures == null)
            {
               serverDynamicFeatures = new CopyOnWriteArraySet<DynamicFeature>(parent.getServerDynamicFeatures());
            }
            serverDynamicFeatures.add((DynamicFeature) provider);
         }
         newContracts.put(DynamicFeature.class, priority);
      }
      if (Utils.isA(provider, AsyncResponseProvider.class, contracts))
      {
         try
         {
            addAsyncResponseProvider((AsyncResponseProvider) provider, provider.getClass(), parent);
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
            addAsyncStreamProvider((AsyncStreamProvider) provider, provider.getClass(), parent);
            int priority = Utils.getPriority(priorityOverride, contracts, AsyncStreamProvider.class, provider.getClass());
            newContracts.put(AsyncStreamProvider.class, priority);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateAsyncStreamProvider(), e);
         }
      }
   }

   protected MediaTypeMap<SortedKey<MessageBodyReader>> getServerMessageBodyReaders(ResteasyProviderFactoryImpl parent)
   {
      if (serverMessageBodyReaders == null && parent != null)
         return parent.getServerMessageBodyReaders();
      return serverMessageBodyReaders;
   }

   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getServerMessageBodyWriters(ResteasyProviderFactoryImpl parent)
   {
      if (serverMessageBodyWriters == null && parent != null)
         return parent.getServerMessageBodyWriters();
      return serverMessageBodyWriters;
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

      if ((type == null || type == RuntimeType.SERVER) && serverMessageBodyReaders == null)
      {
         serverMessageBodyReaders = parent.getServerMessageBodyReaders().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            if (type == null)
            {
               serverMessageBodyReaders.add(MediaType.valueOf(consume), key);
            }
            else if (type == RuntimeType.SERVER)
            {
               serverMessageBodyReaders.add(MediaType.valueOf(consume), key);
            }
         }
      }
      else
      {
         if (type == null)
         {
            serverMessageBodyReaders.add(new MediaType("*", "*"), key);
         }
         else if (type == RuntimeType.SERVER)
         {
            serverMessageBodyReaders.add(new MediaType("*", "*"), key);
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

      if ((type == null || type == RuntimeType.SERVER) && serverMessageBodyWriters == null)
      {
         serverMessageBodyWriters = parent.getServerMessageBodyWriters().clone();
      }
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: " + mime);
            if (type == null)
            {
               serverMessageBodyWriters.add(MediaType.valueOf(consume), key);
            }
            else if (type == RuntimeType.SERVER)
            {
               serverMessageBodyWriters.add(MediaType.valueOf(consume), key);
            }
         }
      }
      else
      {
         //logger.info(">>> Adding provider: " + provider.getClass().getName() + " with mime type of: default */*");
         if (type == null)
         {
            serverMessageBodyWriters.add(new MediaType("*", "*"), key);
         }
         else if (type == RuntimeType.SERVER)
         {
            serverMessageBodyWriters.add(new MediaType("*", "*"), key);
         }
      }
   }

   private void addAsyncResponseProvider(AsyncResponseProvider provider, Class providerClass, ResteasyProviderFactory parent)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncResponseProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncResponseProviders == null)
      {
         asyncResponseProviders = new ConcurrentHashMap<Class<?>, AsyncResponseProvider>();
         asyncResponseProviders.putAll(parent.getAsyncResponseProviders());
      }
      asyncResponseProviders.put(asyncClass, provider);
   }

   private void addAsyncStreamProvider(AsyncStreamProvider provider, Class providerClass, ResteasyProviderFactory parent)
   {
      Type asyncType = Types.getActualTypeArgumentsOfAnInterface(providerClass, AsyncStreamProvider.class)[0];
      Utils.injectProperties(rpf, provider.getClass(), provider);
      Class<?> asyncClass = Types.getRawType(asyncType);
      if (asyncStreamProviders == null)
      {
         asyncStreamProviders = new ConcurrentHashMap<Class<?>, AsyncStreamProvider>();
         asyncStreamProviders.putAll(parent.getAsyncStreamProviders());
      }
      asyncStreamProviders.put(asyncClass, provider);
   }

}
