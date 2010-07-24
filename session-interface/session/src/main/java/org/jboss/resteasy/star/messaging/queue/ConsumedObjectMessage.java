package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.jboss.resteasy.star.messaging.HttpHeaderProperty;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConsumedObjectMessage extends ConsumedMessage
{
   protected Object readObject;
   
   public ConsumedObjectMessage(ClientMessage message)
   {
      super(message);
      if (message.getType() != ClientMessage.OBJECT_TYPE) throw new IllegalArgumentException("Client message must be an OBJECT_TYPE");
   }

   @Override
   public void build(Response.ResponseBuilder builder)
   {
      buildHeaders(builder);
      if (readObject == null)
      {
         int size = message.getBodyBuffer().readInt();
         if (size > 0)
         {
            byte[] body = new byte[size];
            message.getBodyBuffer().readBytes(body);
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            try
            {
               ObjectInputStream ois = new ObjectInputStream(bais);
               readObject = ois.readObject();
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }

      }
      builder.entity(readObject);
   }

}
