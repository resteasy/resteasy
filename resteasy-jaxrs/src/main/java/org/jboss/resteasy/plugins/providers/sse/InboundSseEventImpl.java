package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.sse.InboundSseEvent;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class InboundSseEventImpl implements InboundSseEvent
{
   private final String name;

   private final String id;

   private final String comment;

   private final byte[] data;

   private final long reconnectDelay;

   private final Annotation[] annotations;

   private final MediaType mediaType;

   private final MultivaluedMap<String, String> headers;

   static class Builder
   {
      private String name;

      private String id;

      private long reconnectDelay = -1;

      private final ByteArrayOutputStream dataStream;

      private final Annotation[] annotations;

      private final MediaType mediaType;

      private final MultivaluedMap<String, String> headers;

      private final StringBuilder commentBuilder;

      Builder(Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers)
      {
         this.annotations = annotations;
         this.mediaType = mediaType;
         this.headers = headers;

         this.commentBuilder = new StringBuilder();
         this.dataStream = new ByteArrayOutputStream();
      }

      public Builder name(String name)
      {
         this.name = name;
         return this;
      }

      public Builder id(String id)
      {
         this.id = id;
         return this;
      }

      public Builder commentLine(final CharSequence commentLine)
      {
         if (commentLine != null)
         {
            commentBuilder.append(commentLine).append('\n');
         }

         return this;
      }

      public Builder reconnectDelay(long milliseconds)
      {
         this.reconnectDelay = milliseconds;
         return this;
      }

      public Builder write(byte[] data)
      {
         if (data == null || data.length == 0)
         {
            return this;
         }
         try
         {
            this.dataStream.write(data);
         }
         catch (IOException ex)
         {
            throw new ProcessingException(Messages.MESSAGES.failedToWriteDataToInboudEvent(), ex);
         }
         return this;
      }

      public InboundSseEvent build()
      {
         //from https://html.spec.whatwg.org/multipage/server-sent-events.html#processField
         //If the data buffer's last character is a U+000A LINE FEED (LF) character, 
         //then remove the last character from the data buffer
         return new InboundSseEventImpl(name, id, commentBuilder.length() > 0 ? commentBuilder.substring(0,
               commentBuilder.length() - 1) : null, reconnectDelay, dataStream.toByteArray(), annotations, mediaType,
               headers);
      }
   }

   private InboundSseEventImpl(final String name, final String id, final String comment, final long reconnectDelay,
         final byte[] data, final Annotation[] annotations, final MediaType mediaType,
         final MultivaluedMap<String, String> headers)
   {
      this.name = name;
      this.id = id;
      this.comment = comment;
      this.reconnectDelay = reconnectDelay;
      this.data = data;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
   }

   public String getName()
   {
      return name;
   }

   public String getId()
   {
      return id;
   }

   public String getComment()
   {
      return comment;
   }

   public long getReconnectDelay()
   {
      if (reconnectDelay < 0)
      {
         return -1;
      }
      return reconnectDelay;
   }

   public boolean isReconnectDelaySet()
   {
      return reconnectDelay > -1;
   }

   public boolean isEmpty()
   {
      return data.length == 0;
   }

   public String readData()
   {
      return readData(SseConstants.STRING_AS_GENERIC_TYPE);
   }

   public <T> T readData(Class<T> type)
   {
      return readData(new GenericType<T>(type), MediaType.TEXT_PLAIN_TYPE);
   }

   public <T> T readData(GenericType<T> type)
   {
      return readData(type, MediaType.TEXT_PLAIN_TYPE);
   }

   public <T> T readData(Class<T> messageType, MediaType mediaType)
   {
      return readData(new GenericType<T>(messageType), mediaType);
   }

   public <T> T readData(GenericType<T> type, MediaType mediaType)
   {
      //System.out.println("Thread " + Thread.currentThread().getName() + "read data");
      final MediaType effectiveMediaType = mediaType == null ? this.mediaType : mediaType;
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      RegisterBuiltin.register(factory);
      final MessageBodyReader reader = factory.getClientMessageBodyReader(type.getRawType(), type.getType(),
            annotations, mediaType);
      if (reader == null)
      {
         throw new IllegalStateException(Messages.MESSAGES.notFoundMBR(type.getClass().getName()));
      }
      return readAndCast(type, effectiveMediaType, reader);
   }

   @SuppressWarnings("unchecked")
   private <T> T readAndCast(GenericType<T> type, MediaType effectiveMediaType, MessageBodyReader reader)
   {
      try
      {
         return (T) reader.readFrom(type.getRawType(), type.getType(), annotations, effectiveMediaType, headers,
               new ByteArrayInputStream(data));
      }
      catch (IOException ex)
      {
         throw new ProcessingException(Messages.MESSAGES.failedToReadData(), ex);
      }
   }

   public byte[] getRawData()
   {
      if (data.length == 0)
      {
         return data;
      }

      return Arrays.copyOf(data, data.length);
   }

   @Override
   public String toString()
   {
      String s;

      try
      {
         s = readData();
      }
      catch (ProcessingException e)
      {
         s = "Exception:" + e.getLocalizedMessage();
      }

      return "InboundSseEvent{id=" + id + '\'' + ", comment=" + (comment == null ? "[]" : '\'' + comment + '\'')
            + ", data=" + s + '}';
   }
   
   public MediaType getMediaType()
   {
	   return mediaType;
   }

}
