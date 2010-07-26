package org.hornetq.rest.topic;

import org.hornetq.rest.Constants;
import org.hornetq.rest.LinkHeaderSupport;
import org.hornetq.rest.Message;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicPollerResource
{
   protected CurrentTopicIndex current;
   protected TopicMessageRepository repository;

   public TopicPollerResource(TopicMessageRepository repository, CurrentTopicIndex current)
   {
      this.repository = repository;
      this.current = current;
   }

   @Path("/last")
   @GET
   public Response top(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                       @Context UriInfo info) throws Exception
   {
      TopicMessageIndex top = current.getCurrent();
      if (top.getId() == null)
      {
         return getNext(wait, info, top);
      }
      Response.ResponseBuilder builder = getMessage(info, top.getId());
      builder.header("Content-Location", getContentLocation(info, top.getId()));
      return builder.build();
   }

   @Path("/next")
   @GET
   public Response next(@HeaderParam(Constants.WAIT_HEADER) @DefaultValue("0") long wait,
                        @QueryParam("index") @DefaultValue("") String index,
                        @Context UriInfo info) throws Exception
   {
      if (index.equals(""))
      {
         TopicMessageIndex top = current.getCurrent();
         return getNext(wait, info, top);
      }
      TopicMessageIndex top = repository.getMessageIndex(index);
      if (top == null)
      {
         Response.ResponseBuilder responseBuilder = Response.status(Response.Status.GONE.getStatusCode());
         setTopLink(responseBuilder, info);
         setNextLink(responseBuilder, info);
         return responseBuilder.build();
      }

      return getNext(wait, info, top);
   }

   protected Response.ResponseBuilder getMessage(UriInfo info, String id)
   {
      Message msg = repository.getMessage(id);
      Response.ResponseBuilder responseBuilder = getMessageResponse(info, msg);
      if (msg.getBody() != null)
      {
         responseBuilder.entity(msg.getBody());
         String type = msg.getHeaders().getFirst("Content-Type");
         responseBuilder.type(type);
      }
      else
      {
         responseBuilder.status(204);
      }
      return responseBuilder;
   }

   @Path("/messages/{id}")
   @GET
   public Response getMessageResource(@Context UriInfo info, @PathParam("id") String id)
   {
      Response.ResponseBuilder responseBuilder = getMessage(info, id);
      return responseBuilder.build();
   }

   protected Response.ResponseBuilder getMessageResponse(UriInfo info, Message msg)
   {
      Response.ResponseBuilder responseBuilder = Response.ok();
      setNextLink(responseBuilder, info, msg);
      setDestinationLink(responseBuilder, info);
      for (String header : msg.getHeaders().keySet())
      {
         List values = msg.getHeaders().get(header);
         for (Object value : values) responseBuilder.header(header, value);
      }
      return responseBuilder;
   }

   public static String getContentLocation(UriInfo info, String id)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("messages").path(id);
      return builder.build().toString();
   }

   protected void setNextLink(Response.ResponseBuilder response, UriInfo info, Message msg)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("next");
      builder.queryParam("index", msg.getId());
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "next", "next", uri, null);
   }

   protected void setNextLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("next");
      builder.queryParam("index", "-1");
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "next", "next", uri, null);
   }

   protected Response getNext(long wait, UriInfo info, TopicMessageIndex top)
           throws InterruptedException
   {
      boolean ready = top.getLatch().await(wait, TimeUnit.SECONDS);
      if (!ready)
      {
         return Response.status(503).build();
      }
      String id = top.getNext();
      Response.ResponseBuilder builder = getMessage(info, id);
      builder.header("Content-Location", getContentLocation(info, id));
      return builder.build();
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

   protected void setDestinationLink(Response.ResponseBuilder response, UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      String uri = builder.build().toString();
      LinkHeaderSupport.setLinkHeader(response, "generator", "generator", uri, null);
   }
}
