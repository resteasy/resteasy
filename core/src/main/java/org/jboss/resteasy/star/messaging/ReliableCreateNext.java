package org.jboss.resteasy.star.messaging;

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

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReliableCreateNext
{

   private MessageRepository messageRepository;
   private MessagePublisher publisher;

   public ReliableCreateNext(MessageRepository messageRepository, MessagePublisher publisher)
   {
      this.messageRepository = messageRepository;
      this.publisher = publisher;
   }

   @POST
   public Response redirectCreation(@Context UriInfo uriInfo)
   {
      long id = messageRepository.generateId();
      Response.ResponseBuilder res = Response.status(Response.Status.TEMPORARY_REDIRECT.getStatusCode());
      res.location(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build());
      return res.build();
   }

   @POST
   @Path("{id}")
   public Response create(@PathParam("id") long id, @Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body)
   {
      if (messageRepository.getMessage(id) != null)
      {
         Response.ResponseBuilder builder = Response.status(405);
         builder.entity("Message has already been created").type("text/plain");
         throw new WebApplicationException(builder.build());
      }
      Message message = messageRepository.createMessage(id, headers.getRequestHeaders(), body);
      try
      {
         publisher.publish(message);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      URI location = messageRepository.getMessageUri(id, uriInfo);
      Response.ResponseBuilder builder = Response.created(location);
      String matched = uriInfo.getMatchedURIs().get(1);
      UriBuilder nextBuilder = uriInfo.getBaseUriBuilder();

      long nextId = messageRepository.generateId();
      nextBuilder.path(matched).path(Long.toString(nextId));
      URI next = nextBuilder.build();
      LinkHeaderSupport.setLinkHeader(builder, "create-next", "create-next", next.toString(), "*/*");
      return builder.build();
   }

}
