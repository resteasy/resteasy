package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientSessionFactory;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class DestinationResource
{
   private Map<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();

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
}
