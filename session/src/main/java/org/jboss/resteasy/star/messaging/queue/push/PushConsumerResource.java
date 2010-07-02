package org.jboss.resteasy.star.messaging.queue.push;

import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PushConsumerResource
{
   protected Map<String, PushConsumer> consumers = new ConcurrentHashMap<String, PushConsumer>();
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected final String startup = Long.toString(System.currentTimeMillis());
   protected final AtomicLong sessionCounter = new AtomicLong(1);
   protected PushStore pushStore;

   public void start()
   {

   }

   public void stop()
   {
      for (PushConsumer consumer : consumers.values())
      {
         consumer.stop();
      }
   }

   public PushStore getPushStore()
   {
      return pushStore;
   }

   public void setPushStore(PushStore pushStore)
   {
      this.pushStore = pushStore;
   }

   public void addRegistration(PushRegistration reg) throws Exception
   {
      PushConsumer consumer = new PushConsumer(sessionFactory, destination, reg.getId(), reg);
      consumer.start();
      consumers.put(reg.getId(), consumer);
   }

   @POST
   public Response create(@Context UriInfo uriInfo, PushRegistration registration)
   {
      //System.out.println("PushRegistration: " + registration);

      // todo put some logic here to check for duplicates
      String genId = sessionCounter.getAndIncrement() + "-" + startup;
      registration.setId(genId);
      registration.setDestination(destination);
      PushConsumer consumer = new PushConsumer(sessionFactory, destination, genId, registration);
      try
      {
         consumer.start();
         if (registration.isDurable() && pushStore != null)
         {
            pushStore.add(registration);
         }
      }
      catch (Exception e)
      {
         consumer.stop();
         throw new WebApplicationException(e, Response.serverError().entity("Failed to start consumer.").type("text/plain").build());
      }

      consumers.put(genId, consumer);
      UriBuilder location = uriInfo.getAbsolutePathBuilder();
      location.path(genId);
      return Response.created(location.build()).build();
   }

   @GET
   @Path("{consumer-id")
   @Produces("application/xml")
   public PushRegistration getConsumer(@PathParam("consumer-id") String consumerId)
   {
      PushConsumer consumer = consumers.get(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(404).entity("Could not find consumer.").type("text/plain").build());
      }
      return consumer.getRegistration();
   }

   @DELETE
   @Path("{consumer-id")
   public void deleteConsumer(@PathParam("consumer-id") String consumerId)
   {
      PushConsumer consumer = consumers.remove(consumerId);
      if (consumer == null)
      {
         throw new WebApplicationException(Response.status(404).entity("Could not find consumer.").type("text/plain").build());
      }
      consumer.stop();
   }

   public Map<String, PushConsumer> getConsumers()
   {
      return consumers;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }
}
