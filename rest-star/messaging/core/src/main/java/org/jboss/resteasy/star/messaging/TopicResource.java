package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientSessionFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicResource
{
   private MessageRepository repository;
   private CurrentMessageIndex current;
   private ClientSessionFactory factory;
   private String topicName;
   private SenderResource sender;
   private PollerResource poller;

   public TopicResource(MessageRepository repository, CurrentMessageIndex current, ClientSessionFactory factory, String topicName)
   {
      this.repository = repository;
      this.current = current;
      this.factory = factory;
      this.topicName = topicName;

      sender = new SenderResource(repository, factory, topicName);
      poller = new PollerResource(repository, current);
   }

   @Path("poller")
   public PollerResource poller()
   {
      return poller;
   }

   @Path("sender")
   public SenderResource sender()
   {
      return sender;
   }

   @GET
   @Produces("application/xml")
   public Response get(@Context UriInfo uriInfo)
   {

      String msg = "<topic>"
              + "<name>" + topicName + "</name>"
              + "</topic/>";
      Response.ResponseBuilder builder = Response.ok(msg);
      setSenderLink(builder, uriInfo);
      setTopLink(builder, uriInfo);
      return builder.build();
   }

   @HEAD
   @Produces("application/xml")
   public Response head(@Context UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      setSenderLink(builder, uriInfo);
      setTopLink(builder, uriInfo);
      return builder.build();
   }

   protected void setSenderLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("sender");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "sender", "sender", uri, null);
   }

   protected void setTopLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("poller");
      builder.path("top");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "top-message", "top-message", uri, null);
   }


}
