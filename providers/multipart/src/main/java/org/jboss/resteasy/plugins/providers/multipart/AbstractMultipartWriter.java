package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

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
}
