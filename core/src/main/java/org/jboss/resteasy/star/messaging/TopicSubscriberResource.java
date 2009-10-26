package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientSessionFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicSubscriberResource
{
   private TopicMessageRepository repository;
   private Map<String, ConsumerForwarding> subscriptions = new ConcurrentHashMap<String, ConsumerForwarding>();
   private ClientSessionFactory factory;
   private String topicName;

   public TopicSubscriberResource(String topicName, TopicMessageRepository repository, ClientSessionFactory factory)
   {
      this.repository = repository;
      this.factory = factory;
      this.topicName = topicName;
   }

   @POST
   @Consumes("text/uri-list")
   public Response createSubscriber(@Context UriInfo info, String uri)
   {
      ConsumerForwarding forwarding = new ConsumerForwarding(uri, topicName, factory, repository, false);
      subscriptions.put(forwarding.getId(), forwarding);
      UriBuilder subscriberLocation = info.getBaseUriBuilder();
      subscriberLocation.path("/topics/" + topicName + "/subscribers/" + forwarding.getId());
      return Response.created(subscriberLocation.build()).build();
   }

   @DELETE
   @Path("{id}")
   public void delete(@PathParam("id") String uuid)
   {
      ConsumerForwarding consumer = subscriptions.remove(uuid);
      if (consumer != null) consumer.cleanup();
   }
}
