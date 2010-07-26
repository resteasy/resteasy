package org.hornetq.rest.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.rest.HttpHeaderProperty;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ConsumedMessage
{
   public static final String POSTED_AS_HTTP_MESSAGE = "postedAsHttpMessage";
   protected ClientMessage message;

   public ConsumedMessage(ClientMessage message)
   {
      this.message = message;
   }

   public long getMessageID()
   {
      return message.getMessageID();
   }

   public abstract void build(Response.ResponseBuilder builder);

   protected void buildHeaders(Response.ResponseBuilder builder)
   {
      for (SimpleString key : message.getPropertyNames())
      {
         String k = key.toString();
         String headerName = HttpHeaderProperty.fromPropertyName(k);
         if (headerName == null) continue;
         builder.header(headerName, message.getStringProperty(k));
      }
   }

   public static ConsumedMessage createConsumedMessage(ClientMessage message)
   {
      Boolean aBoolean = message.getBooleanProperty(POSTED_AS_HTTP_MESSAGE);
      if (aBoolean != null && aBoolean.booleanValue())
      {
         return new ConsumedHttpMessage(message);
      }
      else if (message.getType() == ClientMessage.OBJECT_TYPE)
      {
         return new ConsumedObjectMessage(message);
      }
      else
      {
         throw new IllegalArgumentException("ClientMessage must be an HTTP message or an Object message: " + message + " type: " + message.getType());
      }

   }
}
