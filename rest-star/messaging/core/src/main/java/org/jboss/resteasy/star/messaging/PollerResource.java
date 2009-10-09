package org.jboss.resteasy.star.messaging;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.HEAD;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PollerResource
{
   protected CurrentMessageIndex current;
   protected MessageRepository repository;

   public PollerResource(MessageRepository repository, CurrentMessageIndex current)
   {
      this.repository = repository;
      this.current = current;
   }

   @Path("/top")
   @GET
   public Response top(@QueryParam("wait") @DefaultValue("0") long wait,
                       @Context UriInfo info) throws Exception
   {
      MessageIndex top = current.getCurrent();
      System.out.println("TOP id = " + top.getId());
      if (top.getId() == -1)
      {
         return getNext(wait, info, top);
      }
      Response.ResponseBuilder builder = getMessage(info, top.getId());
      builder.header("Content-Location", getContentLocation(info, top.getId()));
      return builder.build();
   }

   @Path("/top")
   @HEAD
   public Response headTop(@QueryParam("wait") @DefaultValue("0") long wait,
                       @Context UriInfo info) throws Exception
   {
      MessageIndex top = current.getCurrent();
      if (top.getId() == -1)
      {
         return getHeadNext(wait, info, top);
      }
      Response.ResponseBuilder builder = getHeadMessage(info, top.getId());
      builder.header("Content-Location", getContentLocation(info, top.getId()));
      return builder.build();
   }

   @Path("/next")
   @GET
   public Response next(@QueryParam("wait") @DefaultValue("0") long wait,
                        @QueryParam("index") long index,
                       @Context UriInfo info) throws Exception
   {
      MessageIndex top = repository.getMessageIndex(index);
      if (top == null)
      {
         Response.ResponseBuilder responseBuilder = Response.status(Response.Status.GONE);
         String link = getTopLink(info);
         responseBuilder.header("Link", link);
         return responseBuilder.build();
      }

      return getNext(wait, info, top);
   }

   @Path("/next")
   @HEAD
   public Response headNext(@QueryParam("wait") @DefaultValue("0") long wait,
                        @QueryParam("index") long index,
                       @Context UriInfo info) throws Exception
   {
      MessageIndex top = repository.getMessageIndex(index);
      if (top == null)
      {
         Response.ResponseBuilder responseBuilder = Response.status(Response.Status.GONE);
         String link = getTopLink(info);
         responseBuilder.header("Link", link);
         return responseBuilder.build();
      }

      return getHeadNext(wait, info, top);
   }

   protected Response.ResponseBuilder getMessage(UriInfo info, long id)
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
   @HEAD
   public Response headMessage(@Context UriInfo info, @PathParam("id") long id)
   {
      Response.ResponseBuilder responseBuilder = getHeadMessage(info, id);
      return responseBuilder.build();
   }

   protected Response.ResponseBuilder getHeadMessage(UriInfo info, long id)
   {
      Message msg = repository.getMessage(id);
      Response.ResponseBuilder responseBuilder = getMessageResponse(info, msg);
      if (msg.getBody() == null)
      {
         String type = msg.getHeaders().getFirst("Content-Type");
         responseBuilder.type(type);
      }
      return responseBuilder;
   }

   protected Response.ResponseBuilder getMessageResponse(UriInfo info, Message msg)
   {
      String link = getNextLink(info, msg);
      Response.ResponseBuilder responseBuilder = Response.ok();
      responseBuilder.header("Link", link);
      responseBuilder.header("Link", getDestinationLink(info));
      for (String header : msg.getHeaders().keySet())
      {
         List values = msg.getHeaders().get(header);
         for (Object value : values) responseBuilder.header(header, value);
      }
      return responseBuilder;
   }

   public static String getContentLocation(UriInfo info, long id)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("/messages/" + id);
      return builder.build().toString();
   }

   protected String getNextLink(UriInfo info, Message msg)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("next");
      builder.queryParam("index", msg.getId());

      String link = "<" + builder.build().toString() + ">; rel=\"next-message\"; title=\"next-message\"";
      return link;
   }

   protected Response getNext(long wait, UriInfo info, MessageIndex top)
           throws InterruptedException
   {
      boolean ready = top.getLatch().await(wait, TimeUnit.SECONDS);
      if (!ready)
      {
         return Response.status(504).build();
      }
      long id = top.getNext();
      Response.ResponseBuilder builder = getMessage(info, id);
      builder.header("Content-Location", getContentLocation(info, id));
      return builder.build();
   }

   protected Response getHeadNext(long wait, UriInfo info, MessageIndex top)
           throws InterruptedException
   {
      boolean ready = top.getLatch().await(wait, TimeUnit.SECONDS);
      if (!ready)
      {
         return Response.status(504).build();
      }
      long id = top.getNext();
      Response.ResponseBuilder builder = getHeadMessage(info, id);
      builder.header("Content-Location", getContentLocation(info, id));
      return builder.build();
   }

   protected String getTopLink(UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      builder.path("poller");
      String link = "<" + builder.build().toString() + ">; rel=\"top-message\"; title=\"top-message\"";
      return link;
   }

   protected String getDestinationLink(UriInfo info)
   {
      String basePath = info.getMatchedURIs().get(1);
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      String link = "<" + builder.build().toString() + ">; rel=\"generator\"; title=\"generator\"";
      return link;
   }
}
