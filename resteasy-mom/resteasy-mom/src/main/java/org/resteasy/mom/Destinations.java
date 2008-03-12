package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class Destinations
{
   private static ConcurrentHashMap<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();
   private static ConcurrentHashMap<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();


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
   public TopicResource getTopic(@PathParam("destination")String name) throws Exception
   {
      return getTopicResource(name);
   }

   public static Destination getDLQ(InitialContext ctx) throws Exception
   {
      return (Destination) ctx.lookup("queue/DLQ");
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
      QueueResource tmp = queues.putIfAbsent(name, queue);
      if (tmp == null)
      {
         System.out.println("created Queue Resource:" + name);
         return queue;
      }
      else
      {
         queue.close();
         return tmp;
      }
   }

   public TopicResource getTopicResource(String name) throws Exception
   {
      TopicResource topic = topics.get(name);
      if (topic != null)
      {
         return topic;
      }

      InitialContext ctx = new InitialContext();
      Destination destination = null;
      try
      {
         destination = (Destination) ctx.lookup("topic/" + name);
      }
      catch (NamingException e)
      {
         throw new WebApplicationException(e, HttpResponseCodes.SC_NOT_FOUND);
      }
      ConnectionFactory factory = getConnectionFactory(ctx);
      topic = new TopicResource(name, factory, factory.createConnection(), destination, getMessageProcessor());
      TopicResource tmp = topics.putIfAbsent(name, topic);
      if (tmp == null)
      {
         System.out.println("created Topic Resource:" + name);
         return topic;
      }
      else
      {
         topic.close();
         return tmp;
      }
   }
}
