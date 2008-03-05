package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Message;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Receiver
{
   private Destination destination;
   private Connection connection;
   private MessageConsumer consumer;
   private Session session;
   private Message currentMessage;
   private MessageProcessor processor;

   public Receiver(Destination destination, Connection connection, MessageProcessor processor) throws Exception
   {
      this.destination = destination;
      this.connection = connection;
      this.processor = processor;
   }

   public MessageConsumer getConsumer() throws Exception
   {
      if (consumer == null)
      {
         close();
         this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

         try
         {
            consumer = session.createConsumer(destination);
            connection.start();
         }
         catch (Exception ex)
         {
            session.close();
            throw ex;
         }
      }
      return consumer;
   }

   public synchronized void close()
   {
      currentMessage = null;
      try
      {
         if (consumer != null) consumer.close();
      }
      catch (JMSException ignored) {}
      consumer = null;

      try
      {
         if (session != null) session.close();
      }
      catch (JMSException ignore) {}
      session = null;
   }

   @POST
   @Path("/head")
   public synchronized Response getAndAcknowledge(@QueryParam("wait") @DefaultValue("-1")long wait) throws Exception
   {
      if (currentMessage != null)
      {
         return Response.status(HttpResponseCodes.SC_CONFLICT).build();
      }
      Message message = getMessage(wait);
      Response response = processor.extractResponse(message);
      message.acknowledge();
      return response;
   }

   private Message getMessage(long wait) throws Exception
   {
      System.out.println("wait time: " + wait);
      Message message = null;
      if (wait < 0) message = getConsumer().receiveNoWait();
      else message = getConsumer().receive(wait);

      if (message == null) throw new WebApplicationException(HttpResponseCodes.SC_REQUEST_TIMEOUT);
      return message;
   }

   @GET
   @Path("/head")
   public synchronized Response get(@QueryParam("wait") @DefaultValue("-1")long wait) throws Exception
   {
      System.out.println("getHead");
      if (currentMessage == null)
      {
         currentMessage = getMessage(wait);
      }

      return processor.extractResponse(currentMessage);

   }

   @DELETE
   @Path("/head")
   public synchronized void acknowledge() throws Exception
   {
      if (currentMessage == null) throw new WebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
      currentMessage.acknowledge();
      currentMessage = null;
   }

}
