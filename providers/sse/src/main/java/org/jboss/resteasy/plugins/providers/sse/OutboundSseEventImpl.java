package org.jboss.resteasy.plugins.providers.sse;

import java.lang.reflect.Type;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.i18n.Messages;

public class OutboundSseEventImpl implements OutboundSseEvent
{
   private final String name;
   private final String comment;
   private final String id;
   private final GenericType type;
   private final MediaType mediaType;
   private final Object data;
   private final long reconnectDelay;

   public static class BuilderImpl implements Builder
   {
      private String name;
      private String comment;
      private String id;
      private long reconnectDelay = -1;
      private GenericType type;
      private Object data;
      private MediaType mediaType = MediaType.SERVER_SENT_EVENTS_TYPE;

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

      public Builder reconnectDelay(long milliseconds)
      {
         if (milliseconds < 0)
         {
            milliseconds = -1;
         }
         this.reconnectDelay = milliseconds;
         return this;
      }

      public Builder mediaType(final MediaType mediaType)
      {
         if (mediaType == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("mediaType"));
         }
         this.mediaType = mediaType;
         return this;
      }

      public Builder comment(String comment)
      {
         this.comment = comment;
         return this;
      }

      public Builder data(Class type, Object data)
      {
         if (type == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("type"));
         }
         if (data == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("data"));
         }

         this.type = new GenericType(type);
         this.data = data;
         return this;
      }

      public Builder data(GenericType type, Object data)
      {
         if (type == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("type"));
         }
         if (data == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("data"));
         }

         this.type = type;
         this.data = data;
         return this;
      }

      public Builder data(Object data)
      {
         if (data == null)
         {
            throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("data"));
         }

         GenericType genericType = null;
         if (data instanceof GenericEntity)
         {
            genericType = new GenericType(((GenericEntity) data).getType());
         }
         else
         {
            genericType = (data == null) ? null : new GenericType(data.getClass());
         }

         return data(genericType, data);
      }

      public OutboundSseEvent build()
      {
         //TODO:check spec to figure out if this requires
         /*if (comment == null)
         {
            if ((data == null) && (type == null))
            {
               throw new IllegalArgumentException(Messages.MESSAGES.nullValueSetToCreateOutboundSseEvent("data and type"));
            }
         }*/

         return new OutboundSseEventImpl(name, id, reconnectDelay, type, mediaType, data, comment);
      }
   }

   OutboundSseEventImpl(final String name, final String id, final long reconnectDelay, final GenericType type,
         final MediaType mediaType, final Object data, final String comment)
   {
      this.name = name;
      this.comment = comment;
      this.id = id;
      this.reconnectDelay = reconnectDelay;
      this.type = type;
      this.mediaType = mediaType;
      this.data = data;
   }

   public String getName()
   {
      return name;
   }

   public String getId()
   {
      return id;
   }

   public long getReconnectDelay()
   {
      return reconnectDelay;
   }

   public boolean isReconnectDelaySet()
   {
      return reconnectDelay > -1;
   }

   public Class<?> getType()
   {
      return type == null ? null : type.getRawType();
   }

   public Type getGenericType()
   {
      return type == null ? null : type.getType();
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public String getComment()
   {
      return comment;
   }

   public Object getData()
   {
      return data;
   }

}
