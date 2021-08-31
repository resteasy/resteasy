package org.jboss.resteasy.plugins.providers;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncStreamingOutput;
import org.jboss.resteasy.util.MediaTypeHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
public class AsyncStreamingOutputProvider implements AsyncMessageBodyWriter<AsyncStreamingOutput>
{
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return AsyncStreamingOutput.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType);
   }

   @Override
   public long getSize(AsyncStreamingOutput streamingOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(AsyncStreamingOutput streamingOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      throw new RuntimeException("Can only be used with async IO");
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(AsyncStreamingOutput streamingOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream)
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      return streamingOutput.asyncWrite(entityStream);
   }
}
