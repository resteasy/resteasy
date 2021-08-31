package org.jboss.resteasy.core.providerfactory;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.util.Map;
import java.util.Set;

/**
 * A ServerHelper that does nothing, useful to save memory when creating a ResteasyProviderFactory for client side only
 */
@SuppressWarnings("rawtypes")
public final class NOOPServerHelper extends ServerHelper
{
   public static final NOOPServerHelper SINGLETON = new NOOPServerHelper();
   private NOOPServerHelper() {
   }

   @Override
   protected void processProviderContracts(Class provider, Integer priorityOverride, boolean isBuiltin, Map<Class<?>, Integer> contracts, Map<Class<?>, Integer> newContracts) {
   }

   @Override
   protected synchronized void processProviderInstanceContracts(Object provider, Map<Class<?>, Integer> contracts, Integer priorityOverride, boolean builtIn, Map<Class<?>, Integer> newContracts) {
   }

   @Override
   protected JaxrsInterceptorRegistry<ContainerRequestFilter> getRequestFiltersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ContainerResponseFilter> getResponseFiltersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Map<Class<?>, AsyncResponseProvider> getAsyncResponseProvidersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Map<Class<?>, AsyncStreamProvider> getAsyncStreamProvidersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   public JaxrsInterceptorRegistry<ContainerRequestFilter> getRequestFilters() {
      throw new UnsupportedOperationException();
   }

   @Override
   public JaxrsInterceptorRegistry<ContainerResponseFilter> getResponseFilters() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<Class<?>, AsyncResponseProvider> getAsyncResponseProviders() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<Class<?>, AsyncStreamProvider> getAsyncStreamProviders() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void addMessageBodyReader(MessageBodyReader provider, Class<?> providerClass, int priority, boolean isBuiltin) {
   }

   @Override
   protected void addMessageBodyWriter(MessageBodyWriter provider, Class<?> providerClass, int priority, boolean isBuiltin) {
   }

   @Override
   protected MediaTypeMap<SortedKey<MessageBodyReader>> getMessageBodyReadersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected MediaTypeMap<SortedKey<MessageBodyWriter>> getMessageBodyWritersForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<ReaderInterceptor> getReaderInterceptorRegistryForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected JaxrsInterceptorRegistry<WriterInterceptor> getWriterInterceptorRegistryForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Set<DynamicFeature> getDynamicFeaturesForWrite() {
      throw new UnsupportedOperationException();
   }

   @Override
   public MediaTypeMap<SortedKey<MessageBodyReader>> getMessageBodyReaders() {
      throw new UnsupportedOperationException();
   }

   @Override
   public MediaTypeMap<SortedKey<MessageBodyWriter>> getMessageBodyWriters() {
      throw new UnsupportedOperationException();
   }

   @Override
   public JaxrsInterceptorRegistry<ReaderInterceptor> getReaderInterceptorRegistry() {
      throw new UnsupportedOperationException();
   }

   @Override
   public JaxrsInterceptorRegistry<WriterInterceptor> getWriterInterceptorRegistry() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<DynamicFeature> getDynamicFeatures() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<Class<?>, SortedKey<ExceptionMapper>> getExceptionMappers() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addExceptionMapper(Class provider, boolean isBuiltin) {
      // complete
   }

   @Override
   public void addAsyncStreamProvider(Class provider) {
      // complete
   }

   @Override
   public void addAsyncResponseProvider(Class provider) {
      // complete
   }

   @Override
   public void addContainerResponseFilter(Class provider, int priority) {
      // complete
   }

   @Override
   public void addContainerRequestFilter(Class provider, int priority) {
      // complete
   }

   @Override
   public void addDynamicFeature(Class provider) {
      // complete
   }

   @Override
   public void addWriterInterceptor(Class provider, int priority) {
      // complete
   }

   @Override
   public void addReaderInterceptor(Class provider, int priority) {
      // complete
   }

   @Override
   public void addWildcardMBR(SortedKey<MessageBodyReader> mbr) {
      // complete
   }

   @Override
   public void addSubtypeWildMBR(MediaType mediaType, SortedKey<MessageBodyReader> mbr) {
      // complete
   }

   @Override
   public void addRegularMBR(MediaType mediaType, SortedKey<MessageBodyReader> mbr) {
      // complete
   }

   @Override
   public void addCompositeWildcardMBR(MediaType mediaType, SortedKey<MessageBodyReader> mbr, String baseSubtype) {
      // complete
   }

   @Override
   public void addWildcardCompositeMBR(MediaType mediaType, SortedKey<MessageBodyReader> mbr, String baseSubtype) {
      // complete
   }

   @Override
   public void addWildcardMBW(SortedKey<MessageBodyWriter> mbw) {
      // complete
   }

   @Override
   public void addRegularMBW(MediaType mediaType, SortedKey<MessageBodyWriter> mbw) {
      // complete
   }

   @Override
   public void addSubtypeWildMBW(MediaType mediaType, SortedKey<MessageBodyWriter> mbw) {
      // complete
   }

   @Override
   public void addCompositeWildcardMBW(MediaType mediaType, SortedKey<MessageBodyWriter> mbw, String baseSubtype) {
      // complete
   }

   @Override
   public void addWildcardCompositeMBW(MediaType mediaType, SortedKey<MessageBodyWriter> mbw, String baseSubtype) {
      // complete
   }
}
