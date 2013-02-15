package org.jboss.resteasy.example.pubsub;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class SubscriptionResource
{
   protected ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<String, Subscriber>();
   protected Executor executor;

   public SubscriptionResource()
   {
      executor = Executors.newSingleThreadExecutor();
   }

   @POST
   @Path("subscribers")
   public Response create(@Context UriInfo uriInfo, final @FormParam("name") String name)
   {
      if (name == null) throw new BadRequestException();
      Subscriber subscriber = new Subscriber(10);
      subscribers.putIfAbsent(name, subscriber);
      return Response.created(uriInfo.getRequestUriBuilder().path(name).build()).build();
   }

   @POST
   @Path("subscription")
   public void post(@Context HttpHeaders headers, final byte[] content)
   {
      final MediaType type = headers.getMediaType();
      executor.execute(new Runnable()
      {
         @Override
         public void run()
         {
            for (Subscriber subscriber : subscribers.values())
            {
               subscriber.post(type, content);
            }

         }
      });
   }

   @GET
   @Path("subscribers/{name}")
   public void longPoll(@PathParam("name") String name, final @Suspended AsyncResponse response)
   {
      final Subscriber subscriber = subscribers.get(name);
      if (subscriber == null) throw new NotFoundException();
      executor.execute(new Runnable()
      {
         @Override
         public void run()
         {
            subscriber.poll(response);
         }
      });
   }
}
