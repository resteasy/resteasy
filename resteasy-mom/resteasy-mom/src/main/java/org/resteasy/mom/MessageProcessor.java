package org.resteasy.mom;

import org.resteasy.util.HttpHeaderNames;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageProcessor
{
   private Connection connection;
   private Destination dlq;
   private int bufferSize = 100;

   public MessageProcessor(Connection connection, Destination dlq, int bufferSize) throws Exception
   {
      this.connection = connection;
      this.dlq = dlq;
      this.bufferSize = bufferSize;
   }

   public String createSelector(HttpHeaders headers)
   {
      StringBuffer selector = new StringBuffer();

      boolean first = true;

      for (MediaType type : headers.getAcceptableMediaTypes())
      {
         if (type.isWildcardType()) return "";
         if (first)
         {
            first = false;
         }
         else
         {
            selector.append(" OR ");
         }
         selector.append(toJavaIdentifier(HttpHeaderNames.CONTENT_TYPE));
         if (type.isWildcardSubtype())
         {
            selector.append(" LIKE '")
                    .append(type.getType())
                    .append("/%'");
         }
         else
         {
            selector.append(" = '")
                    .append(type.toString())
                    .append("'");
         }
      }
      return selector.toString();
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


   /**
    * Replaces '-' with '$'
    *
    * @param str
    * @return
    */
   protected String toJavaIdentifier(String str)
   {
      return str.replace('-', '$').toLowerCase();
   }

   protected String toHeaderName(String str)
   {
      return str.replace('$', '-').toLowerCase();
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

   public Map<String, String> extractHeaders(Message message) throws JMSException
   {
      Map<String, String> rtn = new HashMap<String, String>();

      Enumeration en = message.getPropertyNames();
      while (en.hasMoreElements())
      {
         String key = (String) en.nextElement();
         if (!key.startsWith("JMS"))
         {
            String value = message.getStringProperty(key);
            System.out.println("header: " + toHeaderName(key) + " value: " + value);
            rtn.put(toHeaderName(key), value);
         }
      }
      return rtn;
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

   public void deadletter(Message message)
   {
      try
      {
         if (connection == null || dlq == null) return;
         System.out.println("DEAD LETTER!!!!");
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         try
         {
            MessageProducer producer = session.createProducer(dlq);
            producer.send(message);
            System.out.println("SENT DEAD LETTER");
         }
         catch (JMSException e)
         {
         }
         finally
         {
            session.close();
         }
      }
      catch (JMSException ignored)
      {

      }
   }


}
