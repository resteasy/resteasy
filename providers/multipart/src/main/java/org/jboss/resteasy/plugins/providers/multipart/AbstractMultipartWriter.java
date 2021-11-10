package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractMultipartWriter
{
   protected static final byte[] DOUBLE_DASH_BYTES = "--".getBytes(StandardCharsets.US_ASCII);
   protected static final byte[] LINE_SEPARATOR_BYTES = "\r\n".getBytes(StandardCharsets.US_ASCII);
   protected static final byte[] COLON_SPACE_BYTES = ": ".getBytes(StandardCharsets.US_ASCII);

   @Context
   protected Providers workers;

   protected void write(MultipartOutput multipartOutput, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null)
         boundary = multipartOutput.getBoundary();
      httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + multipartOutput.getBoundary());
      byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);

      writeParts(multipartOutput, entityStream, boundaryBytes);
      entityStream.write(boundaryBytes);
      entityStream.write(DOUBLE_DASH_BYTES);
   }

   protected void writeParts(MultipartOutput multipartOutput, OutputStream entityStream, byte[] boundaryBytes)
         throws IOException
   {
      for (OutputPart part : multipartOutput.getParts())
      {
         MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
         writePart(entityStream, boundaryBytes, part, headers);
      }
   }

   @SuppressWarnings(value = "unchecked")
   protected void writePart(OutputStream entityStream, byte[] boundaryBytes, OutputPart part, MultivaluedMap<String, Object> headers)
         throws IOException
   {
      entityStream.write(boundaryBytes);
      entityStream.write(LINE_SEPARATOR_BYTES);
      headers.putAll(part.getHeaders());
      headers.putSingle(HttpHeaderNames.CONTENT_TYPE, part.getMediaType());

      Object entity = part.getEntity();
      Class<?> entityType = part.getType();
      Type entityGenericType = part.getGenericType();
      MessageBodyWriter writer = workers.getMessageBodyWriter(entityType, entityGenericType, null, part.getMediaType());
      LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
      OutputStream partStream = new DelegatingOutputStream(entityStream) {
         @Override
         public void close() throws IOException {
            // no close
            // super.close();
         }
      };
      writer.writeTo(entity, entityType, entityGenericType, null, part.getMediaType(), headers, new HeaderFlushedOutputStream(headers, partStream));
      entityStream.write(LINE_SEPARATOR_BYTES);
   }

   protected CompletionStage<Void> asyncWrite(MultipartOutput multipartOutput, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream)
     {
        String boundary = mediaType.getParameters().get("boundary");
        if (boundary == null)
           boundary = multipartOutput.getBoundary();
        httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + multipartOutput.getBoundary());
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);

        return asyncWriteParts(multipartOutput, entityStream, boundaryBytes)
                .thenCompose(v -> entityStream.asyncWrite(boundaryBytes))
                .thenCompose(v -> entityStream.asyncWrite(DOUBLE_DASH_BYTES));
     }

     protected CompletionStage<Void> asyncWriteParts(MultipartOutput multipartOutput, AsyncOutputStream entityStream, byte[] boundaryBytes)
     {
        CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
        for (OutputPart part : multipartOutput.getParts())
        {
           MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
           ret = ret.thenCompose(v -> asyncWritePart(entityStream, boundaryBytes, part, headers));
        }
        return ret;
     }

     @SuppressWarnings(value = "unchecked")
     protected CompletionStage<Void> asyncWritePart(AsyncOutputStream entityStream, byte[] boundaryBytes, OutputPart part, MultivaluedMap<String, Object> headers)
     {
        headers.putAll(part.getHeaders());
        headers.putSingle(HttpHeaderNames.CONTENT_TYPE, part.getMediaType());

        Object entity = part.getEntity();
        Class<?> entityType = part.getType();
        Type entityGenericType = part.getGenericType();
        AsyncMessageBodyWriter writer = (AsyncMessageBodyWriter) workers.getMessageBodyWriter(entityType, entityGenericType, null, part.getMediaType());
        LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());
        return entityStream.asyncWrite(boundaryBytes)
                .thenCompose(v -> entityStream.asyncWrite(LINE_SEPARATOR_BYTES))
                .thenCompose(v -> writer.asyncWriteTo(entity, entityType, entityGenericType, null, part.getMediaType(), headers, new HeaderFlushedAsyncOutputStream(headers, entityStream)))
                .thenCompose(v -> entityStream.asyncWrite(LINE_SEPARATOR_BYTES));
     }
}
