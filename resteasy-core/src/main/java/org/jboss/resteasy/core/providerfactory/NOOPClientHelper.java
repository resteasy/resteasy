package org.jboss.resteasy.core.providerfactory;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;

/**
 * A ClientHelper that does nothing, useful to save memory when creating a ResteasyProviderFactory for server side only
 */
@SuppressWarnings("rawtypes")
public final class NOOPClientHelper extends ClientHelper
{
   public static final NOOPClientHelper INSTANCE = new NOOPClientHelper(null);

   public NOOPClientHelper(final ResteasyProviderFactoryImpl rpf)
   {
      super(rpf);
   }

   @Override
   protected void initializeDefault()
   {
      //NOOP
   }

   @Override
   protected void initialize(ResteasyProviderFactoryImpl parent)
   {
      //NOOP
   }

   @Override
   protected void initializeClientProviders(ResteasyProviderFactory factory)
   {
      //NOOP
   }

   @Override
   protected JaxrsInterceptorRegistry<ReaderInterceptor> getClientReaderInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<WriterInterceptor> getClientWriterInterceptorRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ClientRequestFilter> getClientRequestFilterRegistry(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ClientResponseFilter> getClientResponseFilters(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Set<DynamicFeature> getClientDynamicFeatures(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Map<Class<?>, AsyncClientResponseProvider> getAsyncClientResponseProviders(ResteasyProviderFactory parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected RxInvokerProvider<?> getRxInvokerProviderFromReactiveClass(Class<?> clazz)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected boolean isReactive(Class<?> clazz)
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
   protected MediaTypeMap<SortedKey<MessageBodyReader>> getClientMessageBodyReaders(ResteasyProviderFactoryImpl parent)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getClientMessageBodyWriters(ResteasyProviderFactoryImpl parent)
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
