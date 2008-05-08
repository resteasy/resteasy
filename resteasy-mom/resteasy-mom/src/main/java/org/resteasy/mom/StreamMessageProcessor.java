package org.resteasy.mom;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StreamMessageProcessor extends MessageProcessor
{
   private int bufferSize = 100;

   public StreamMessageProcessor(int bufferSize) throws Exception
   {
      this.bufferSize = bufferSize;
   }

   public Message createMessage(HttpHeaders headers, InputStream entityStream, Session session)
           throws JMSException, IOException
   {
      StreamMessage message = session.createStreamMessage();
      byte[] buffer = new byte[bufferSize];
      int wasRead = 0;
      do
      {
         wasRead = entityStream.read(buffer);
         if (wasRead > 0)
         {
            System.out.println("SENT " + wasRead + " bytes!");
            message.writeBytes(buffer, 0, wasRead);
         }
      } while (wasRead > -1);

      for (String key : headers.getRequestHeaders().keySet())
      {
         message.setStringProperty(toJavaIdentifier(key.toLowerCase()), headers.getRequestHeaders().getFirst(key));
      }
      return message;
   }


   public Response extractResponse(Message message)
           throws JMSException
   {
      if (message instanceof StreamMessage) return extractStreamResponse((StreamMessage) message);

      throw new WebApplicationException(500);
   }


   protected Response extractStreamResponse(StreamMessage message)
           throws JMSException
   {
      byte[] body = extractBody(message);


      System.out.println("RECEIVED body length: " + body.length);
      Response.ResponseBuilder builder = Response.ok(body);

      Enumeration en = message.getPropertyNames();
      while (en.hasMoreElements())
      {
         String key = (String) en.nextElement();
         if (!key.startsWith("JMS"))
         {
            String value = message.getStringProperty(key);
            System.out.println("header: " + toHeaderName(key) + " value: " + value);
            builder.header(toHeaderName(key), value);
         }
      }
      return builder.build();
   }

   public byte[] extractBody(Message message)
           throws JMSException
   {
      // todo need to use OutputStream when available
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      System.out.println("Extracting body");

      StreamMessage stream = (StreamMessage) message;
      byte[] buffer = new byte[bufferSize];

      try
      {
         int wasRead = 0;
         do
         {
            System.out.println("LOOP!");
            wasRead = stream.readBytes(buffer);
            if (wasRead > 0)
            {
               System.out.println("EXTRACTED: " + wasRead);
               baos.write(buffer, 0, wasRead);
            }
            else
            {
               System.out.println("Nothing extracted, wasRead: " + wasRead);
            }
         } while (wasRead == bufferSize || wasRead == -1);
         byte[] body = baos.toByteArray();
         return body;
      }
      finally
      {
         stream.reset();
      }
   }


}
