package org.jboss.resteasy.core;

import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.ThreadLocalStack;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Allow applications to push/pop provider factories onto the stack
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ThreadLocalResteasyProviderFactory extends ResteasyProviderFactory implements ProviderFactoryDelegate
{
   private static final ThreadLocalStack<ResteasyProviderFactory> delegate = new ThreadLocalStack<ResteasyProviderFactory>();

   private ResteasyProviderFactory defaultFactory;


   public ThreadLocalResteasyProviderFactory(ResteasyProviderFactory defaultFactory)
   {
      this.defaultFactory = defaultFactory;
   }

   public ResteasyProviderFactory getDelegate()
   {
      ResteasyProviderFactory factory = delegate.get();
      if (factory == null) return defaultFactory;
      return factory;
   }

   @Override
   public void addExceptionMapper(ExceptionMapper provider, Type exceptionType)
   {
      getDelegate().addExceptionMapper(provider, exceptionType);
   }

   @Override
   public void addContextResolver(Class<? extends ContextResolver> resolver, boolean builtin)
   {
      getDelegate().addContextResolver(resolver, builtin);
   }

   @Override
   public void addContextResolver(ContextResolver provider, boolean builtin)
   {
      getDelegate().addContextResolver(provider, builtin);
   }

   @Override
   public void addContextResolver(ContextResolver provider, Type typeParameter)
   {
      getDelegate().addContextResolver(provider, typeParameter);
   }

   @Override
   public void addContextResolver(ContextResolver provider, Type typeParameter, boolean builtin)
   {
      getDelegate().addContextResolver(provider, typeParameter, builtin);
   }

   @Override
   public void addStringConverter(StringConverter provider, Type typeParameter)
   {
      getDelegate().addStringConverter(provider, typeParameter);
   }

   @Override
   public boolean isRegisterBuiltins()
   {
      return getDelegate().isRegisterBuiltins();
   }

   @Override
   public void setRegisterBuiltins(boolean registerBuiltins)
   {
      getDelegate().setRegisterBuiltins(registerBuiltins);
   }

   @Override
   public InjectorFactory getInjectorFactory()
   {
      return getDelegate().getInjectorFactory();
   }

   @Override
   public void setInjectorFactory(InjectorFactory injectorFactory)
   {
      getDelegate().setInjectorFactory(injectorFactory);
   }

   @Override
   public void injectProperties(Object o)
   {
      getDelegate().injectProperties(o);
   }

   @Override
   public void addStringParameterUnmarshaller(Class<? extends StringParameterUnmarshaller> resolver)
   {
      getDelegate().addStringParameterUnmarshaller(resolver);
   }

   @Override
   public void addClientErrorInterceptor(ClientErrorInterceptor handler)
   {
      getDelegate().addClientErrorInterceptor(handler);
   }

   @Override
   public List<ClientErrorInterceptor> getClientErrorInterceptors()
   {
      return getDelegate().getClientErrorInterceptors();
   }

   @Override
   public <T> StringParameterUnmarshaller<T> createStringParameterUnmarshaller(Class<T> clazz)
   {
      return getDelegate().createStringParameterUnmarshaller(clazz);
   }

   @Override
   public boolean isBuiltinsRegistered()
   {
      return getDelegate().isBuiltinsRegistered();
   }

   @Override
   public void setBuiltinsRegistered(boolean builtinsRegistered)
   {
      getDelegate().setBuiltinsRegistered(builtinsRegistered);
   }

   @Override
   public void addStringConverter(StringConverter provider, Class providerClass)
   {
      super.addStringConverter(provider, providerClass);
   }

   @Override
   public void addExceptionMapper(ExceptionMapper provider, Class providerClass)
   {
      getDelegate().addExceptionMapper(provider, providerClass);
   }

   @Override
   public void addMessageBodyReader(MessageBodyReader provider, Class providerClass, boolean isBuiltin)
   {
      getDelegate().addMessageBodyReader(provider, providerClass, isBuiltin);
   }

   @Override
   public void addMessageBodyWriter(MessageBodyWriter provider, Class providerClass, boolean isBuiltin)
   {
      getDelegate().addMessageBodyWriter(provider, providerClass, isBuiltin);
   }

   @Override
   public void addContextResolver(ContextResolver provider, Class root, boolean builtin)
   {
      getDelegate().addContextResolver(provider, root, builtin);
   }

   @Override
   public void addContextResolver(ContextResolver provider, Type typeParameter, Class providerClass, boolean builtin)
   {
      getDelegate().addContextResolver(provider, typeParameter, providerClass, builtin);
   }

   @Override
   public void addMessageBodyReader(MessageBodyReader provider, boolean isBuiltin)
   {
      getDelegate().addMessageBodyReader(provider, isBuiltin);
   }

   @Override
   public void addMessageBodyWriter(MessageBodyWriter provider, boolean isBuiltin)
   {
      getDelegate().addMessageBodyWriter(provider, isBuiltin);
   }

   @Override
   public InterceptorRegistry<MessageBodyReaderInterceptor> getServerMessageBodyReaderInterceptorRegistry()
   {
      return getDelegate().getServerMessageBodyReaderInterceptorRegistry();
   }

   @Override
   public InterceptorRegistry<ClientExecutionInterceptor> getClientExecutionInterceptorRegistry()
   {
      return getDelegate().getClientExecutionInterceptorRegistry();
   }

   @Override
   public InterceptorRegistry<MessageBodyWriterInterceptor> getClientMessageBodyWriterInterceptorRegistry()
   {
      return getDelegate().getClientMessageBodyWriterInterceptorRegistry();
   }

   @Override
   public InterceptorRegistry<MessageBodyReaderInterceptor> getClientMessageBodyReaderInterceptorRegistry()
   {
      return getDelegate().getClientMessageBodyReaderInterceptorRegistry();
   }

   @Override
   public InterceptorRegistry<PreProcessInterceptor> getServerPreProcessInterceptorRegistry()
   {
      return getDelegate().getServerPreProcessInterceptorRegistry();
   }

   @Override
   public InterceptorRegistry<PostProcessInterceptor> getServerPostProcessInterceptorRegistry()
   {
      return getDelegate().getServerPostProcessInterceptorRegistry();
   }


   @Override
   public InterceptorRegistry<MessageBodyWriterInterceptor> getServerMessageBodyWriterInterceptorRegistry()
   {
      return getDelegate().getServerMessageBodyWriterInterceptorRegistry();
   }

   @Override
   protected void initialize()
   {

   }

   public static void push(ResteasyProviderFactory factory)
   {
      delegate.push(factory);
   }

   public static void pop()
   {
      delegate.pop();
   }

   @Override
   public UriBuilder createUriBuilder()
   {
      return getDelegate().createUriBuilder();
   }

   @Override
   public Response.ResponseBuilder createResponseBuilder()
   {
      return getDelegate().createResponseBuilder();
   }

   @Override
   public Variant.VariantListBuilder createVariantListBuilder()
   {
      return getDelegate().createVariantListBuilder();
   }

   @Override
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      return getDelegate().createHeaderDelegate(tClass);
   }

   @Override
   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      getDelegate().addHeaderDelegate(clazz, header);
   }

   @Override
   public void addMessageBodyReader(Class<? extends MessageBodyReader> provider)
   {
      getDelegate().addMessageBodyReader(provider);
   }

   @Override
   public void addMessageBodyReader(MessageBodyReader provider)
   {
      getDelegate().addMessageBodyReader(provider);
   }

   @Override
   public void addBuiltInMessageBodyReader(MessageBodyReader provider)
   {
      getDelegate().addBuiltInMessageBodyReader(provider);
   }

   @Override
   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> provider)
   {
      getDelegate().addMessageBodyWriter(provider);
   }

   @Override
   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      getDelegate().addMessageBodyWriter(provider);
   }

   @Override
   public void addBuiltInMessageBodyWriter(MessageBodyWriter provider)
   {
      getDelegate().addBuiltInMessageBodyWriter(provider);
   }

   @Override
   public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyReader(type, genericType, annotations, mediaType);
   }

   @Override
   public void addExceptionMapper(Class<? extends ExceptionMapper> provider)
   {
      getDelegate().addExceptionMapper(provider);
   }

   @Override
   public void addExceptionMapper(ExceptionMapper provider)
   {
      getDelegate().addExceptionMapper(provider);
   }

   @Override
   public void addContextResolver(Class<? extends ContextResolver> resolver)
   {
      getDelegate().addContextResolver(resolver);
   }

   @Override
   public void addContextResolver(ContextResolver provider)
   {
      getDelegate().addContextResolver(provider);
   }

   @Override
   public void addStringConverter(Class<? extends StringConverter> resolver)
   {
      getDelegate().addStringConverter(resolver);
   }

   @Override
   public void addStringConverter(StringConverter provider)
   {
      getDelegate().addStringConverter(provider);
   }

   @Override
   public List<ContextResolver> getContextResolvers(Class<?> clazz, MediaType type)
   {
      return getDelegate().getContextResolvers(clazz, type);
   }

   @Override
   public StringConverter getStringConverter(Class<?> clazz)
   {
      return getDelegate().getStringConverter(clazz);
   }

   @Override
   public void registerProvider(Class provider)
   {
      getDelegate().registerProvider(provider);
   }

   @Override
   public void registerProviderInstance(Object provider)
   {
      getDelegate().registerProviderInstance(provider);
   }

   @Override
   public <T> T getProvider(Class<T> providerClass)
   {
      return getDelegate().getProvider(providerClass);
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type)
   {
      return getDelegate().getExceptionMapper(type);
   }

   @Override
   public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return getDelegate().getMessageBodyWriter(type, genericType, annotations, mediaType);
   }

   @Override
   public <T> T createEndpoint(Application applicationConfig, Class<T> endpointType)
           throws IllegalArgumentException, UnsupportedOperationException
   {
      return getDelegate().createEndpoint(applicationConfig, endpointType);
   }

   @Override
   public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType)
   {
      return getDelegate().getContextResolver(contextType, mediaType);
   }

   @Override
   public void addMessageBodyReader(Class<? extends MessageBodyReader> provider, boolean isBuiltin)
   {
      getDelegate().addMessageBodyReader(provider, isBuiltin);
   }

   @Override
   public void addMessageBodyWriter(Class<? extends MessageBodyWriter> provider, boolean isBuiltin)
   {
      getDelegate().addMessageBodyWriter(provider, isBuiltin);
   }

   @Override
   public void registerProvider(Class provider, boolean isBuiltin)
   {
      getDelegate().registerProvider(provider, isBuiltin);
   }

   @Override
   public void appendInterceptorPrecedence(String precedence)
   {
      getDelegate().appendInterceptorPrecedence(precedence);
   }

   @Override
   public void insertInterceptorPrecedenceAfter(String after, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceAfter(after, newPrecedence);
   }

   @Override
   public void insertInterceptorPrecedenceBefore(String before, String newPrecedence)
   {
      getDelegate().insertInterceptorPrecedenceBefore(before, newPrecedence);
   }
}
