package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Connection;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class Destinations
{
   private static Map<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();


   protected static ConnectionFactory getConnectionFactory(InitialContext ctx) throws Exception
   {
      return (ConnectionFactory) ctx.lookup("java:/ConnectionFactory");
   }

   private static MessageProcessor processor;

   private synchronized static MessageProcessor getMessageProcessor() throws Exception
   {
      if (processor == null)
      {
         InitialContext ctx = new InitialContext();
         Connection connection = getConnectionFactory(ctx).createConnection();
         processor = new MessageProcessor(connection, getDLQ(ctx), 100);
         
      }
      return processor;
   }


   @Path("/queues/{destination}")
   public QueueResource getQueue(@PathParam("destination")String name) throws Exception
   {
      return getQueueResource(name);
   }

   @Path("/topics/{destination}")
   public QueueResource getTopic(@PathParam("destination")String name) throws Exception
   {
      return getQueueResource(name);
   }

   public static Destination getDLQ(InitialContext ctx) throws Exception
   {
      return (Destination)ctx.lookup("queue/DLQ");
   }

   public QueueResource getQueueResource(String name) throws Exception
   {
      QueueResource queue = queues.get(name);
      if (queue != null)
      {
         return queue;
      }
      
      InitialContext ctx = new InitialContext();
      Destination destination = null;
      try
      {
         destination = (Destination) ctx.lookup("queue/" + name);
      }
      catch (NamingException e)
      {
         throw new WebApplicationException(e, HttpResponseCodes.SC_NOT_FOUND);
      }
      queue = new QueueResource(name, getConnectionFactory(ctx).createConnection(), destination, getMessageProcessor());
      queues.put(name, queue);
      System.out.println("created Queue Resource:" + name);
      return queue;
   }
}
