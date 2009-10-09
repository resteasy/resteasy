package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Message
{
   private long id = -1;
   private long nextMessage;
   private MultivaluedMap<String, String> headers;
   private byte[] body;

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public long getNextMessage()
   {
      return nextMessage;
   }

   public void setNextMessage(long nextMessage)
   {
      this.nextMessage = nextMessage;
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.headers = headers;
   }

   public byte[] getBody()
   {
      return body;
   }

   public void setBody(byte[] body)
   {
      this.body = body;
   }

}
