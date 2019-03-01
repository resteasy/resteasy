package org.jboss.resteasy.core.providerfactory;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;

/**
 * A ServerHelper that does nothing, useful to save memory when creating a ResteasyProviderFactory for client side only
 */
@SuppressWarnings("rawtypes")
public final class NOOPServerHelper extends ServerHelper
{
   public static final NOOPServerHelper INSTANCE = new NOOPServerHelper(null);

   private NOOPServerHelper(final ResteasyProviderFactoryImpl rpf)
   {
      super(rpf);
   }

   @Override
   protected void initialize(ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   @Override
   protected JaxrsInterceptorRegistry<ReaderInterceptor> getServerReaderInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<WriterInterceptor> getServerWriterInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ContainerRequestFilter> getContainerRequestFilterRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ContainerResponseFilter> getContainerResponseFilterRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Set<DynamicFeature> getServerDynamicFeatures(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin,
         Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   @Override
   protected void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts,
         Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   @Override
   protected MediaTypeMap<SortedKey<MessageBodyReader>> getServerMessageBodyReaders(ResteasyProviderFactoryImpl parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getServerMessageBodyWriters(ResteasyProviderFactoryImpl parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void addMessageBodyReader(MessageBodyReader provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   @Override
   protected void addMessageBodyWriter(MessageBodyWriter provider, Class<?> providerClass, int priority,
         boolean isBuiltin, ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }
}
