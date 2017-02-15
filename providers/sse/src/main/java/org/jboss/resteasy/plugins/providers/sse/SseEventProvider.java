package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.OutboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Produces({"text/event-stream"})
@Consumes({"text/event-stream"})
public class SseEventProvider implements MessageBodyWriter<OutboundSseEvent>, MessageBodyReader<SseEventInputImpl>
{
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return OutboundSseEvent.class.isAssignableFrom(type) && MediaType.SERVER_SENT_EVENTS_TYPE.isCompatible(mediaType);
   }

   @Override
   public long getSize(OutboundSseEvent t, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(OutboundSseEvent event, Class<?> type, Type genericType, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      Charset charset = SseConstants.UTF8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      if (event.getComment() != null)
      {
         for (final String comment : event.getComment().split("\n"))
         {
            entityStream.write(SseConstants.COMMENT_LEAD);
            entityStream.write(comment.getBytes(charset));
            entityStream.write(SseConstants.EOL);
         }
      }

      if (event.getType() != null)
      {
         if (event.getName() != null)
         {
            entityStream.write(SseConstants.NAME_LEAD);
            entityStream.write(event.getName().getBytes(charset));
            entityStream.write(SseConstants.EOL);
         }
         if (event.getId() != null)
         {
            entityStream.write(SseConstants.ID_LEAD);
            entityStream.write(event.getId().getBytes(charset));
            entityStream.write(SseConstants.EOL);
         }
         if (event.getReconnectDelay() > -1)
         {
            entityStream.write(SseConstants.RETRY_LEAD);
            entityStream.write(Long.toString(event.getReconnectDelay()).getBytes(charset));
            entityStream.write(SseConstants.EOL);
         }

         if (event.getData() != null)
         {
            Class<?> payloadClass = event.getType();
            Type payloadType = event.getGenericType();
            if (payloadType == null)
            {
               payloadType = payloadClass;
            }

            if (payloadType == null && payloadClass == null)
            {
               payloadType = Object.class;
               payloadClass = Object.class;
            }

            entityStream.write(SseConstants.DATA_LEAD);
            MessageBodyWriter writer = ResteasyProviderFactory.getInstance().getMessageBodyWriter(payloadClass,
                  payloadType, annotations, event.getMediaType());

            if (writer == null)
            {
               throw new ServerErrorException(Messages.MESSAGES.notFoundMBW(payloadClass.getName()),
                     Response.Status.INTERNAL_SERVER_ERROR);
            }

            writer.writeTo(event.getData(), payloadClass, payloadType, annotations, event.getMediaType(), httpHeaders,
                  entityStream);
            entityStream.write(SseConstants.EOL);
         }

      }
   }
   
   
   @Override
   public boolean isReadable(Class<?> cls, Type type, Annotation[] annotations, MediaType mediaType)
   {
      return SseEventInputImpl.class.isAssignableFrom(cls) && MediaType.SERVER_SENT_EVENTS_TYPE.isCompatible(mediaType);
   }

   @Override
   public SseEventInputImpl readFrom(Class<SseEventInputImpl> cls, Type type, Annotation[] annotations,
         MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      return new SseEventInputImpl(annotations, mediaType, httpHeaders, entityStream);
   }

}
