package org.hornetq.rest;

import org.hornetq.rest.queue.QueueResource;
import org.hornetq.rest.topic.TopicResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class DestinationResource
{
   private Map<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();
   private Map<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();

   @Path("/topics/{name}")
   public TopicResource findTopic(@PathParam("name") String name)
   {
      TopicResource topic = topics.get(name);
      if (topic == null) throw new WebApplicationException(404);
      return topic;
   }

   public Map<String, TopicResource> getTopics()
   {
      return topics;
   }

   @Path("/queues/{name}")
   public QueueResource findQueue(@PathParam("name") String name)
   {
      QueueResource queue = queues.get(name);
      if (queue == null) throw new WebApplicationException(404);
      return queue;
   }

   public Map<String, QueueResource> getQueues()
   {
      return queues;
   }
}
