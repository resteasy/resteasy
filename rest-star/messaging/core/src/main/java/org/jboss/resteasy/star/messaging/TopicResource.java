package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.client.ClientSessionFactory;

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
   private TopicMessageRepository repository;
   private CurrentTopicIndex current;
   private ClientSessionFactory factory;
   private String topicName;
   private Object sender;
   private TopicPollerResource poller;
   private TopicSubscriberResource subscribers;

   public TopicResource(TopicMessageRepository repository, CurrentTopicIndex current, ClientSessionFactory factory, String topicName, Object sender)
   {
      this.repository = repository;
      this.current = current;
      this.factory = factory;
      this.topicName = topicName;
      this.sender = sender;

      poller = new TopicPollerResource(repository, current);
      subscribers = new TopicSubscriberResource(topicName, repository, factory);
   }


   @Path("poller")
   public TopicPollerResource poller()
   {
      return poller;
   }

   @Path("create-next")
   public Object sender()
   {
      return sender;
   }

   @Path("subscribers")
   public TopicSubscriberResource subscribers()
   {
      return subscribers;
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
      setSubscribersLink(builder, uriInfo);
      return builder.build();
   }

   @HEAD
   @Produces("application/xml")
   public Response head(@Context UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      setSenderLink(builder, uriInfo);
      setTopLink(builder, uriInfo);
      setNextLink(builder, uriInfo);
      setSubscribersLink(builder, uriInfo);
      return builder.build();
   }

   protected void setSenderLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("create-next");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "create-next", "create-next", uri, null);
   }

   protected void setSubscribersLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("subscribers");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "subscribers", "subscribers", uri, null);
   }

   protected void setTopLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("poller");
      builder.path("last");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "last", "last", uri, null);
   }

   protected void setNextLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("poller");
      builder.path("next");
      builder.queryParam("index", "-1");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "next", "next", uri, null);
   }


}
