package org.jboss.resteasy.mom;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
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
public class ByteArrayMessageProcessor extends MessageProcessor
{
   private int bufferSize = 1000;

   public ByteArrayMessageProcessor() throws Exception
   {
   }

   public Message createMessage(HttpHeaders headers, InputStream entityStream, Session session)
           throws JMSException, IOException
   {
      BytesMessage message = session.createBytesMessage();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[bufferSize];
      int wasRead = 0;
      do
      {
         wasRead = entityStream.read(buffer);
         if (wasRead > 0)
         {
            //System.out.println("SENT " + wasRead + " bytes!");
            baos.write(buffer, 0, wasRead);
         }
      } while (wasRead > -1);

      for (String key : headers.getRequestHeaders().keySet())
      {
         message.setStringProperty(toJavaIdentifier(key.toLowerCase()), headers.getRequestHeaders().getFirst(key));
      }
      message.setIntProperty("httpMessageSize", buffer.length);
      buffer = baos.toByteArray();
      message.writeBytes(buffer);

      return message;
   }


   public Response extractResponse(Message message)
           throws JMSException
   {
      if (message instanceof BytesMessage) return extractStreamResponse((BytesMessage) message);

      throw new WebApplicationException(500);
   }


   protected Response extractStreamResponse(BytesMessage message)
           throws JMSException
   {
      byte[] body = extractBody(message);


      //System.out.println("RECEIVED body length: " + body.length);
      Response.ResponseBuilder builder = Response.ok(body);

      Enumeration en = message.getPropertyNames();
      while (en.hasMoreElements())
      {
         String key = (String) en.nextElement();
         if (!key.startsWith("JMS"))
         {
            String value = message.getStringProperty(key);
            //System.out.println("header: " + toHeaderName(key) + " value: " + value);
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
      //System.out.println("Extracting body");

      BytesMessage stream = (BytesMessage) message;
      int size = message.getIntProperty("httpMessageSize");
      byte[] buffer = new byte[size];
      stream.readBytes(buffer);
      stream.reset();
      return buffer;
   }


}