package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.specimpl.BuiltResponse;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbortedResponse extends ClientResponse
{
   protected InputStream is;

   public AbortedResponse(ClientConfiguration configuration, Response response)
   {
      super(configuration);
      setMetadata(response.getMetadata());
      setStatus(response.getStatus());
      setEntity(response.getEntity());
      if (response instanceof BuiltResponse) {
         BuiltResponse built = (BuiltResponse) response;
         setEntityClass(built.getEntityClass());
         setGenericType(built.getGenericType());
         setAnnotations(built.getAnnotations());
      }

      // spec requires that aborting a request still acts like it went over the wire
      // so we must marshall the entity and buffer it.
      if (response.getEntity() != null) {
         MediaType mediaType = getMediaType();
         if (mediaType == null) {
            mediaType = MediaType.WILDCARD_TYPE;
            getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, mediaType);
         }

         if (!(response.getEntity() instanceof InputStream)) {

            MessageBodyWriter writer = configuration
                    .getMessageBodyWriter(getEntityClass(), getGenericType(),
                            null, mediaType);
            if (writer == null) {
               throw new ProcessingException("Failed to buffer aborted response. Could not find writer for content-type "
                       + mediaType + " type: " + entityClass.getName());
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
               writer.writeTo(getEntity(), getEntityClass(), getGenericType(), getAnnotations(), mediaType, getHeaders(), baos);
            }
            catch (IOException e) {
               throw new ProcessingException("Failed to buffer aborted response", e);
            }
            bufferedEntity = baos.toByteArray();
            setInputStream(new ByteArrayInputStream(bufferedEntity));
         }
         else
         {
            InputStream is = (InputStream)response.getEntity();
            setInputStream(is);
         }
         setEntity(null); // clear all entity information
      }


   }

   @Override
   protected InputStream getInputStream()
   {
      if (is == null && entity != null && entity instanceof InputStream) {
         is = (InputStream) entity;
      }
      return is;
   }

   @Override
   protected void setInputStream(InputStream is)
   {
      this.is = is;
   }

   @Override
   protected void releaseConnection()
   {
      try {
         if (is != null) is.close();
      }
      catch (IOException e) {

      }
   }
}
