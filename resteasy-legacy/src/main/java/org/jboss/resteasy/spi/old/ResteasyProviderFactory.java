package org.jboss.resteasy.spi.old;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.DecoderPrecedence;
import org.jboss.resteasy.annotations.interception.EncoderPrecedence;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.RedirectPrecedence;
import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.LegacyPrecedence;
import org.jboss.resteasy.core.interception.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerRequestFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.WriterInterceptorRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ClientRequestFilterRegistry;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Deprecated
public class ResteasyProviderFactory extends org.jboss.resteasy.spi.ResteasyProviderFactory
{
   protected Map<Class<?>, ClientExceptionMapper> clientExceptionMappers;
   protected LegacyPrecedence precedence;

   protected InterceptorRegistry<ClientExecutionInterceptor> clientExecutionInterceptorRegistry;

   protected List<ClientErrorInterceptor> clientErrorInterceptors;

   public ResteasyProviderFactory()
   {
      super();
   }

   /**
    * Copies a specific component registry when a new
    * provider is added. Otherwise delegates to the parent.
    *
    * @param parent provider factory
    */
   public ResteasyProviderFactory(ResteasyProviderFactory parent)
   {
      super(parent, false);
   }

   /**
    * If local is true, copies components needed by client configuration,
    * so that parent is not referenced. 
    * @param parent provider factory
    * @param local is local
    */
   public ResteasyProviderFactory(ResteasyProviderFactory parent, boolean local)
   {
      super(parent, local);
   }

   protected void initialize()
   {
      super.initialize();
      clientExceptionMappers = new ConcurrentHashMap<Class<?>, ClientExceptionMapper>();
      clientExecutionInterceptorRegistry = new InterceptorRegistry<ClientExecutionInterceptor>(ClientExecutionInterceptor.class, this);
      clientErrorInterceptors = new CopyOnWriteArrayList<ClientErrorInterceptor>();
      registerDefaultInterceptorPrecedences();
   }
   
   protected void initializeRegistriesAndFilters()
   {
      precedence = new LegacyPrecedence();
      serverReaderInterceptorRegistry = new ReaderInterceptorRegistry(this, precedence);
      serverWriterInterceptorRegistry = new WriterInterceptorRegistry(this, precedence);
      containerRequestFilterRegistry = new ContainerRequestFilterRegistry(this, precedence);
      containerResponseFilterRegistry = new ContainerResponseFilterRegistry(this, precedence);

      clientRequestFilterRegistry = new ClientRequestFilterRegistry(this);
      clientRequestFilters = new JaxrsInterceptorRegistry<ClientRequestFilter>(this, ClientRequestFilter.class);
      clientResponseFilters = new ClientResponseFilterRegistry(this);
      clientReaderInterceptorRegistry = new ReaderInterceptorRegistry(this, precedence);
      clientWriterInterceptorRegistry = new WriterInterceptorRegistry(this, precedence);

   }

   protected Map<Class<?>, ClientExceptionMapper> getClientExceptionMappers()
   {
      if (clientExceptionMappers == null && parent != null && parent instanceof ResteasyProviderFactory) return ((ResteasyProviderFactory)parent).getClientExceptionMappers();
      return clientExceptionMappers;
   }

   protected LegacyPrecedence getPrecedence()
   {
      if (precedence == null && parent != null) return ((ResteasyProviderFactory)parent).getPrecedence();
      return precedence;
   }
   
   protected void registerDefaultInterceptorPrecedences(InterceptorRegistry registry)
   {
      // legacy
      registry.appendPrecedence(SecurityPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(HeaderDecoratorPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(EncoderPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(RedirectPrecedence.PRECEDENCE_STRING);
      registry.appendPrecedence(DecoderPrecedence.PRECEDENCE_STRING);

   }

   protected void registerDefaultInterceptorPrecedences()
   {
      precedence.addPrecedence(SecurityPrecedence.PRECEDENCE_STRING, Priorities.AUTHENTICATION);
      precedence.addPrecedence(HeaderDecoratorPrecedence.PRECEDENCE_STRING, Priorities.HEADER_DECORATOR);
      precedence.addPrecedence(EncoderPrecedence.PRECEDENCE_STRING, Priorities.ENTITY_CODER);
      precedence.addPrecedence(RedirectPrecedence.PRECEDENCE_STRING, Priorities.ENTITY_CODER + 50);
      precedence.addPrecedence(DecoderPrecedence.PRECEDENCE_STRING, Priorities.ENTITY_CODER);

     registerDefaultInterceptorPrecedences(getClientExecutionInterceptorRegistry());
   }
   
   /**
    * Append interceptor predence
    *
    * @param precedence precedence
    */
   public void appendInterceptorPrecedence(String precedence)
   {
      if (this.precedence == null)
      {
         this.precedence = ((ResteasyProviderFactory)parent).getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.appendPrecedence(precedence);
      clientExecutionInterceptorRegistry.appendPrecedence(precedence);
   }

   /**
    * @param after         put newPrecedence after this
    * @param newPrecedence new precedence
    */
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      if (this.precedence == null)
     {
         this.precedence = ((ResteasyProviderFactory)parent).getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.insertPrecedenceAfter(after, newPrecedence);

      getClientExecutionInterceptorRegistry().insertPrecedenceAfter(after, newPrecedence);
   }

  /**
    * @param before        put newPrecedence before this
    * @param newPrecedence new precedence
    */
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      if (this.precedence == null)
      {
         this.precedence = ((ResteasyProviderFactory)parent).getPrecedence().clone();
      }
      if (clientExecutionInterceptorRegistry == null)
      {
         clientExecutionInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry().cloneTo(this);
      }
      this.precedence.insertPrecedenceBefore(before, newPrecedence);

      getClientExecutionInterceptorRegistry().insertPrecedenceBefore(before, newPrecedence);
   }

   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      if (clientExecutionInterceptorRegistry == null && parent != null)
         return ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry();
      return clientExecutionInterceptorRegistry;
   }
   
   @Override
   public ReaderInterceptorRegistry getServerReaderInterceptorRegistry()
   {
      if (serverReaderInterceptorRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getServerReaderInterceptorRegistry();
      return (ReaderInterceptorRegistry)serverReaderInterceptorRegistry;
   }

   @Override
   public WriterInterceptorRegistry getServerWriterInterceptorRegistry()
   {
      if (serverWriterInterceptorRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getServerWriterInterceptorRegistry();
      return (WriterInterceptorRegistry)serverWriterInterceptorRegistry;
   }

   @Override
   public ContainerRequestFilterRegistry getContainerRequestFilterRegistry()
   {
      if (containerRequestFilterRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getContainerRequestFilterRegistry();
      return (ContainerRequestFilterRegistry)containerRequestFilterRegistry;
   }

   @Override
   public ContainerResponseFilterRegistry getContainerResponseFilterRegistry()
   {
      if (containerResponseFilterRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getContainerResponseFilterRegistry();
      return (ContainerResponseFilterRegistry)containerResponseFilterRegistry;
   }

   @Override
   public ReaderInterceptorRegistry getClientReaderInterceptorRegistry()
   {
      if (clientReaderInterceptorRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getClientReaderInterceptorRegistry();
      return (ReaderInterceptorRegistry)clientReaderInterceptorRegistry;
   }

   @Override
   public WriterInterceptorRegistry getClientWriterInterceptorRegistry()
   {
      if (clientWriterInterceptorRegistry == null && parent != null) return ((ResteasyProviderFactory)parent).getClientWriterInterceptorRegistry();
      return (WriterInterceptorRegistry)clientWriterInterceptorRegistry;
   }

   @Override
   public JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilters()
   {
      if (clientRequestFilters == null && parent != null) return ((ResteasyProviderFactory)parent).getClientRequestFilters();
      return (JaxrsInterceptorRegistry<ClientRequestFilter>)clientRequestFilters;
   }

   @Override
   public ClientResponseFilterRegistry getClientResponseFilters()
   {
      if (clientResponseFilters == null && parent != null) return ((ResteasyProviderFactory)parent).getClientResponseFilters();
      return (ClientResponseFilterRegistry)clientResponseFilters;
   }
   
   public void addClientExceptionMapper(Class<? extends ClientExceptionMapper<?>> providerClass)
   {
      ClientExceptionMapper<?> provider = createProviderInstance(providerClass);
      addClientExceptionMapper(provider, providerClass);
   }

   public void addClientExceptionMapper(ClientExceptionMapper<?> provider)
   {
      addClientExceptionMapper(provider, provider.getClass());
   }

   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Class<?> providerClass)
   {
      Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(providerClass, ClientExceptionMapper.class)[0];
      addClientExceptionMapper(provider, exceptionType);
   }

   public void addClientExceptionMapper(ClientExceptionMapper<?> provider, Type exceptionType)
   {
     injectProperties(provider.getClass());

      Class<?> exceptionClass = Types.getRawType(exceptionType);
      if (!Throwable.class.isAssignableFrom(exceptionClass))
      {
         throw new RuntimeException(Messages.MESSAGES.incorrectTypeParameterClientExceptionMapper());
      }
      if (clientExceptionMappers == null)
      {
         clientExceptionMappers = new ConcurrentHashMap<Class<?>, ClientExceptionMapper>();
         clientExceptionMappers.putAll(((ResteasyProviderFactory)parent).getClientExceptionMappers());
      }
      clientExceptionMappers.put(exceptionClass, provider);
   }

   /**
    * Add a {@link ClientErrorInterceptor} to this provider factory instance.
    * Duplicate handlers are ignored. (For Client Proxy API only)
    * @param handler client error interceptor
    */
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
      if (clientErrorInterceptors == null)
     {
         clientErrorInterceptors = new CopyOnWriteArrayList<ClientErrorInterceptor>(((ResteasyProviderFactory)parent).getClientErrorInterceptors());
      }
     if (!clientErrorInterceptors.contains(handler))
      {
         clientErrorInterceptors.add(handler);
      }
   }


  /**
    * Return the list of currently registered {@link ClientErrorInterceptor} instances.
    * @return list of client error interceptors
    */
   public List<ClientErrorInterceptor> getClientErrorInterceptors()
   {
      if (clientErrorInterceptors == null && parent != null) return ((ResteasyProviderFactory)parent).getClientErrorInterceptors();
      return clientErrorInterceptors;
   }

   protected void addContextResolver(Class<? extends ContextResolver> resolver, boolean builtin)
   {
      ContextResolver writer = createProviderInstance(resolver);
      addContextResolver(writer, resolver, builtin);
   }

   @Override
   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts)
   {
      super.processProviderContracts(provider, priorityOverride, isBuiltin, contracts, newContracts);
      if (isA(provider, ClientExceptionMapper.class, contracts))
      {
         try
         {
            addClientExceptionMapper(provider);
            newContracts.put(ClientExceptionMapper.class,
                  getPriority(priorityOverride, contracts, ClientExceptionMapper.class, provider));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateClientExceptionMapper(), e);
         }
      }
      if (isA(provider, ClientExecutionInterceptor.class, contracts))
      {
         if (clientExecutionInterceptorRegistry == null)
         {
            clientExecutionInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry().cloneTo(this);
         }
         clientExecutionInterceptorRegistry.register(provider);
         newContracts.put(ClientExecutionInterceptor.class, 0);
      }
      if (isA(provider, PreProcessInterceptor.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = ((ResteasyProviderFactory)parent).getContainerRequestFilterRegistry().clone(this);
         }
         ((ContainerRequestFilterRegistry)containerRequestFilterRegistry).registerLegacy(provider);
         newContracts.put(PreProcessInterceptor.class, 0);
      }
      if (isA(provider, PostProcessInterceptor.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = ((ResteasyProviderFactory)parent).getContainerResponseFilterRegistry().clone(this);
         }
         ((ContainerResponseFilterRegistry)containerResponseFilterRegistry).registerLegacy(provider);
         newContracts.put(PostProcessInterceptor.class, 0);
      }
      if (isA(provider, MessageBodyWriterInterceptor.class, contracts))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = ((ResteasyProviderFactory)parent).getServerWriterInterceptorRegistry().clone(this);
            }
            ((WriterInterceptorRegistry)serverWriterInterceptorRegistry).registerLegacy(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientWriterInterceptorRegistry().clone(this);
            }
            ((WriterInterceptorRegistry)clientWriterInterceptorRegistry).registerLegacy(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class)
               && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException(Messages.MESSAGES.interceptorClassMustBeAnnotated());
         }
         newContracts.put(MessageBodyWriterInterceptor.class, 0);

      }
      if (isA(provider, MessageBodyReaderInterceptor.class, contracts))
      {
         if (provider.isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = ((ResteasyProviderFactory)parent).getServerReaderInterceptorRegistry().clone(this);
            }
            ((ReaderInterceptorRegistry)serverReaderInterceptorRegistry).registerLegacy(provider);
         }
         if (provider.isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientReaderInterceptorRegistry().clone(this);
            }
            ((ReaderInterceptorRegistry)clientReaderInterceptorRegistry).registerLegacy(provider);
         }
         if (!provider.isAnnotationPresent(ServerInterceptor.class)
               && !provider.isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException(Messages.MESSAGES.interceptorClassMustBeAnnotated());
         }
         newContracts.put(MessageBodyReaderInterceptor.class, 0);
      }
   }

   @Override
   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts)
   {
      super.processProviderInstanceContracts(provider, contracts, priorityOverride, builtIn, newContracts);
      if (isA(provider, ClientExceptionMapper.class, contracts))
      {
         try
         {
            addClientExceptionMapper((ClientExceptionMapper) provider);
            newContracts.put(ClientExceptionMapper.class, 0);
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateExceptionMapper(), e);
         }
      }
      if (isA(provider, ClientExecutionInterceptor.class, contracts))
      {
         if (clientExecutionInterceptorRegistry == null)
         {
            clientExecutionInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientExecutionInterceptorRegistry().cloneTo(this);
         }
         clientExecutionInterceptorRegistry.register((ClientExecutionInterceptor) provider);
         newContracts.put(ClientExecutionInterceptor.class, 0);
      }
      if (isA(provider, PreProcessInterceptor.class, contracts))
      {
         if (containerRequestFilterRegistry == null)
         {
            containerRequestFilterRegistry = ((ResteasyProviderFactory)parent).getContainerRequestFilterRegistry().clone(this);
         }
         ((ContainerRequestFilterRegistry)containerRequestFilterRegistry).registerLegacy((PreProcessInterceptor) provider);
         newContracts.put(PreProcessInterceptor.class, 0);
      }
      if (isA(provider, PostProcessInterceptor.class, contracts))
      {
         if (containerResponseFilterRegistry == null)
         {
            containerResponseFilterRegistry = ((ResteasyProviderFactory)parent).getContainerResponseFilterRegistry().clone(this);
         }
         ((ContainerResponseFilterRegistry)containerResponseFilterRegistry).registerLegacy((PostProcessInterceptor) provider);
         newContracts.put(PostProcessInterceptor.class, 0);
      }
      if (isA(provider, MessageBodyWriterInterceptor.class, contracts))
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverWriterInterceptorRegistry == null)
            {
               serverWriterInterceptorRegistry = ((ResteasyProviderFactory)parent).getServerWriterInterceptorRegistry().clone(this);
            }
            ((WriterInterceptorRegistry)serverWriterInterceptorRegistry).registerLegacy((MessageBodyWriterInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientWriterInterceptorRegistry == null)
            {
               clientWriterInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientWriterInterceptorRegistry().clone(this);
            }
            ((WriterInterceptorRegistry)clientWriterInterceptorRegistry).registerLegacy((MessageBodyWriterInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class)
               && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException(Messages.MESSAGES.interceptorClassMustBeAnnotatedWithClass(provider.getClass()));
         }
         newContracts.put(MessageBodyWriterInterceptor.class, 0);
      }
      if (isA(provider, MessageBodyReaderInterceptor.class, contracts))
      {
         if (provider.getClass().isAnnotationPresent(ServerInterceptor.class))
         {
            if (serverReaderInterceptorRegistry == null)
            {
               serverReaderInterceptorRegistry = ((ResteasyProviderFactory)parent).getServerReaderInterceptorRegistry().clone(this);
            }
            ((ReaderInterceptorRegistry)serverReaderInterceptorRegistry).registerLegacy((MessageBodyReaderInterceptor) provider);
         }
         if (provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            if (clientReaderInterceptorRegistry == null)
            {
               clientReaderInterceptorRegistry = ((ResteasyProviderFactory)parent).getClientReaderInterceptorRegistry().clone(this);
            }
            ((ReaderInterceptorRegistry)clientReaderInterceptorRegistry).registerLegacy((MessageBodyReaderInterceptor) provider);
         }
         if (!provider.getClass().isAnnotationPresent(ServerInterceptor.class)
               && !provider.getClass().isAnnotationPresent(ClientInterceptor.class))
         {
            throw new RuntimeException(Messages.MESSAGES.interceptorClassMustBeAnnotatedWithClass(provider.getClass()));
         }
         newContracts.put(MessageBodyReaderInterceptor.class, 0);

      }
   }

   public <T extends Throwable> ClientExceptionMapper<T> getClientExceptionMapper(Class<T> type)
   {
      return getClientExceptionMappers().get(type);
   }
}
