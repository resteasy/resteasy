package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.jboss.resteasy.star.messaging.HttpHeaderProperty;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConsumedHttpMessage extends ConsumedMessage
{
   private byte[] data;

   public ConsumedHttpMessage(ClientMessage message)
   {
      super(message);
   }

   @Override
   public void build(Response.ResponseBuilder builder)
   {
      buildHeaders(builder);
      if (data == null)
      {
         int size = message.getBodyBuffer().readInt();
         if (size > 0)
         {
            data = new byte[size];
            message.getBodyBuffer().readBytes(data);
         }
         else
         {
            data = new byte[0];
         }
      }
      builder.entity(data);
   }


}
