package org.jboss.resteasy.mom;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class Receiver
{
   protected Destination destination;
   protected Connection connection;
   protected MessageConsumer consumer;
   protected Session session;
   protected Message currentMessage;
   protected MessageProcessor processor;
   protected String selector;

   public Receiver(Connection connection, String selector, Destination destination, MessageProcessor processor)
   {
      this.connection = connection;
      this.selector = selector;
      this.destination = destination;
      this.processor = processor;
   }

   protected Receiver(String selector, Destination destination, MessageProcessor processor)
   {
      this.selector = selector;
      this.destination = destination;
      this.processor = processor;
   }

   public MessageConsumer getConsumer() throws Exception
   {
      if (consumer == null)
      {
         this.session = getConnection().createSession(false, Session.CLIENT_ACKNOWLEDGE);

         try
         {
            createConsumer();
            //System.out.println("COnnection started");
            getConnection().start();
         }
         catch (Exception ex)
         {
            session.close();
            throw ex;
         }
      }
      return consumer;
   }

   protected void createConsumer()
           throws JMSException
   {
      consumer = session.createConsumer(destination, selector);
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

   protected Message getMessage(long wait) throws Exception
   {
      //System.out.println("wait time: " + wait);
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
      //System.out.println("getHead");
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
      if (currentMessage == null) return;
      currentMessage.acknowledge();
      currentMessage = null;
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

   protected Connection getConnection()
   {
      return connection;
   }
}
