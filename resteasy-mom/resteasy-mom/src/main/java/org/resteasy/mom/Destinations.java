package org.resteasy.mom;

import org.resteasy.util.HttpResponseCodes;

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
   private ConcurrentHashMap<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();
   private ConcurrentHashMap<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();

   private ConnectionFactory factory;
   private MessageProcessor processor;
   private String queueJndiPrefix = "queue/";
   private String topicJndiPrefix = "topic/";


   public void addQueue(String name, Destination queue) throws Exception
   {
      QueueResource res = new QueueResource(name, factory.createConnection(), queue, processor);
      queues.put(name, res);
   }

   public void addTopic(String name, Destination topic) throws Exception
   {
      TopicResource res = new TopicResource(name, factory, factory.createConnection(), topic, processor);
      topics.put(name, res);
   }

   public void setQueueJndiPrefix(String queueJndiPrefix)
   {
      this.queueJndiPrefix = queueJndiPrefix;
   }

   public void setTopicJndiPrefix(String topicJndiPrefix)
   {
      this.topicJndiPrefix = topicJndiPrefix;
   }

   public void setFactory(ConnectionFactory factory)
   {
      this.factory = factory;
   }

   public void setProcessor(MessageProcessor processor)
   {
      this.processor = processor;
   }

   public void stop()
   {
      for (QueueResource queue : queues.values())
      {
         try
         {
            queue.close();
         }
         catch (Exception ignored)
         {
            ignored.printStackTrace();
         }
      }
      for (TopicResource topic : topics.values())
      {
         try
         {
            topic.close();
         }
         catch (Exception ignored)
         {
            ignored.printStackTrace();
         }
      }
      processor.close();
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
         destination = (Destination) ctx.lookup(queueJndiPrefix + name);
      }
      catch (NamingException e)
      {
         throw new WebApplicationException(e, HttpResponseCodes.SC_NOT_FOUND);
      }
      queue = new QueueResource(name, factory.createConnection(), destination, processor);
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
         destination = (Destination) ctx.lookup(topicJndiPrefix + name);
      }
      catch (NamingException e)
      {
         throw new WebApplicationException(e, HttpResponseCodes.SC_NOT_FOUND);
      }
      topic = new TopicResource(name, factory, factory.createConnection(), destination, processor);
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
