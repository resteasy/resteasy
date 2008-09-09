package org.jboss.resteasy.mom;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DestinationResource
{
   protected Connection connection;
   protected Destination destination;
   protected String name;
   protected MessageProcessor processor;

   public DestinationResource(MessageProcessor processor, String name, Connection connection, Destination destination)
   {
      this.processor = processor;
      this.name = name;
      this.connection = connection;
      this.destination = destination;
   }

   @GET
   @Produces("text/html")
   public String details()
   {
      return "<html><body><h1>Destination: " + name + "</h1></body></html>";
   }

   @POST
   public void send(@Context HttpHeaders headers,
                    @QueryParam("persistent") @DefaultValue("true")boolean persistent,
                    @QueryParam("priority") @DefaultValue("4")int priority,
                    @QueryParam("timeToLive") @DefaultValue("0")long timeToLive,
                    InputStream entityStream) throws Exception
   {
      //System.out.println("SENDING MESSAGE!");
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      try
      {
         MessageProducer producer = session.createProducer(destination);
         try
         {
            Message message = processor.createMessage(headers, entityStream, session);
            int deliveryMode = persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
            producer.send(message, deliveryMode, priority, timeToLive);
         }
         finally
         {
            producer.close();
         }
      }
      finally
      {
         session.close();
      }
   }

   public void close() throws Exception
   {
      connection.close();
   }

}
