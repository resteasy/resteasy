package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.core.interception.jaxrs.AsyncMessageBodyWriter;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractMultipartWriter
{
   @Context
   protected Providers workers;

   protected void write(MultipartOutput multipartOutput, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null)
         boundary = multipartOutput.getBoundary();
      httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + multipartOutput.getBoundary());
      byte[] boundaryBytes = ("--" + boundary).getBytes();

      writeParts(multipartOutput, entityStream, boundaryBytes);
      entityStream.write(boundaryBytes);
      entityStream.write("--".getBytes());
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
      entityStream.write("\r\n".getBytes());
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
      entityStream.write("\r\n".getBytes());
   }

   protected CompletionStage<Void> asyncWrite(MultipartOutput multipartOutput, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream)
     {
        String boundary = mediaType.getParameters().get("boundary");
        if (boundary == null)
           boundary = multipartOutput.getBoundary();
        httpHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, mediaType.toString() + "; boundary=" + multipartOutput.getBoundary());
        byte[] boundaryBytes = ("--" + boundary).getBytes();

        return asyncWriteParts(multipartOutput, entityStream, boundaryBytes)
                .thenCompose(v -> entityStream.rxWrite(boundaryBytes))
                .thenCompose(v -> entityStream.rxWrite("--".getBytes()));
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
        return entityStream.rxWrite(boundaryBytes)
                .thenCompose(v -> entityStream.rxWrite("\r\n".getBytes()))
                .thenCompose(v -> writer.asyncWriteTo(entity, entityType, entityGenericType, null, part.getMediaType(), headers, new HeaderFlushedAsyncOutputStream(headers, entityStream)))
                .thenCompose(v -> entityStream.rxWrite("\r\n".getBytes()));
     }
}
