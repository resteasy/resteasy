package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

/**
 * implements reliable "create", "create-next" pattern defined by REST-* Messaging specificaiton
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMessageNoDups extends PostMessage
{
   private AtomicLong counter = new AtomicLong(1);
   private final String startupTime = Long.toString(System.currentTimeMillis());

   protected String generateDupId()
   {
      return startupTime + Long.toString(counter.incrementAndGet());
   }

   @POST
   public Response redirectCreation(@Context UriInfo uriInfo)
   {
      String id = generateDupId();
      Response.ResponseBuilder res = Response.status(Response.Status.TEMPORARY_REDIRECT.getStatusCode());
      res.location(uriInfo.getAbsolutePathBuilder().path(id).build());
      return res.build();
   }

   public void publish(HttpHeaders headers, byte[] body, String dup, boolean durable) throws Exception
   {
      Pooled pooled = getPooled();
      try
      {
         ClientProducer producer = pooled.producer;
         ClientMessage message = createHornetQMessage(headers, body, durable, pooled.session);
         message.putStringProperty(ClientMessage.HDR_DUPLICATE_DETECTION_ID.toString(), dup);
         producer.send(message);
         pool.add(pooled);
      }
      catch (Exception ex)
      {
         try
         {
            pooled.session.close();
         }
         catch (HornetQException e)
         {
         }
         addPooled();
         throw ex;
      }
   }

   @POST
   @Path("{id}")
   public Response create(@PathParam("id") String dupId, @Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body)
   {
      String matched = uriInfo.getMatchedURIs().get(1);
      UriBuilder nextBuilder = uriInfo.getBaseUriBuilder();
      String nextId = generateDupId();
      nextBuilder.path(matched).path(nextId);
      URI next = nextBuilder.build();


      try
      {
         publish(headers, body, dupId, defaultDurable);
      }
      catch (Exception e)
      {
         Response error = Response.serverError()
                 .entity("Problem posting message: " + e.getMessage())
                 .type("text/plain")
                 .build();
         throw new WebApplicationException(e, error);
      }
      Response.ResponseBuilder builder = Response.status(201);
      serviceManager.getLinkStrategy().setLinkHeader(builder, "create-next", "create-next", next.toString(), "*/*");
      return builder.build();
   }

}