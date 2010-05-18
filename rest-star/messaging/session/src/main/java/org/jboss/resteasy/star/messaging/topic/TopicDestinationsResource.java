package org.jboss.resteasy.star.messaging.topic;

import org.jboss.resteasy.star.messaging.queue.QueueResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/topics")
public class TopicDestinationsResource
{
   private Map<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();

   @Path("/{topic-name}")
   public TopicResource findQueue(@PathParam("topic-name") String name)
   {
      TopicResource queue = topics.get(name);
      if (queue == null) throw new WebApplicationException(404);
      return queue;
   }

   public Map<String, TopicResource> getTopics()
   {
      return topics;
   }
}