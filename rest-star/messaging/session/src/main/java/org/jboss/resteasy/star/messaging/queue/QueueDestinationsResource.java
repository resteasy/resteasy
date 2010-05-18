package org.jboss.resteasy.star.messaging.queue;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/queues")
public class QueueDestinationsResource
{
   private Map<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();

   @Path("/{queue-name}")
   public QueueResource findQueue(@PathParam("queue-name") String name)
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
