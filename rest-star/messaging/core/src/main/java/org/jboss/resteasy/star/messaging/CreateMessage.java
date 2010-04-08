package org.jboss.resteasy.star.messaging;

import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Implements simple "create" link.  Returns 201 with Location of created resource as per HTTP
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CreateMessage
{
   private MessageRepository messageRepository;
   private MessagePublisher publisher;

   public CreateMessage(MessageRepository messageRepository, MessagePublisher publisher)
   {
      this.messageRepository = messageRepository;
      this.publisher = publisher;
   }

   @POST
   public Response create(@Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body)
   {
      Message message = messageRepository.createMessage(headers.getRequestHeaders(), body);
      try
      {
         publisher.publish(message);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      long id = message.getId();
      URI location = messageRepository.getMessageUri(id, uriInfo);
      Response.ResponseBuilder builder = Response.created(location);
      UriBuilder nextBuilder = uriInfo.getAbsolutePathBuilder();

      URI next = nextBuilder.build();
      LinkHeaderSupport.setLinkHeader(builder, "create-next", "create-next", next.toString(), "*/*");
      return builder.build();
   }

}