package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientSessionFactory;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SenderResource
{
   private MessageRepository repository;
   private ClientSessionFactory sessionFactory;
   private String destination;

   public SenderResource(MessageRepository repository, ClientSessionFactory sessionFactory, String destination)
   {
      this.repository = repository;
      this.sessionFactory = sessionFactory;
      this.destination = destination;
   }

   @POST
   public Response post(@Context HttpHeaders headers, @Context UriInfo uriInfo, byte[] body) throws Exception
   {
      Sender sender = new Sender();
      sender.setSessionFactory(sessionFactory);
      sender.setRepository(repository);
      sender.setDestination(destination);

      Message msg = sender.post(headers.getRequestHeaders(), body);
      String link = PollerResource.getContentLocation(uriInfo, msg.getId());
      return Response.created(new URI(link)).build();
   }
}
