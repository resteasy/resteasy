package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
public class ResponseResource {
   public static final String ENTITY = "ENtiTy";

   @GET
   @Path("empty")
   public Response empty() {
      return Response.ok().build();
   }

   @HEAD
   @Path("head")
   public String head() {
      return "head";
   }

   @GET
   @Path("entity")
   @Produces(MediaType.TEXT_PLAIN)
   public String entity() {
      return ENTITY;
   }

   @GET
   @Path("date")
   public String date(@QueryParam("date") String date) {
      return date;
   }

   @POST
   @Path("link")
   public Response getLink(String rel) {
      Response.ResponseBuilder builder = Response.ok();
      if (rel != null && rel.length() != 0) {
         builder.links(createLink("path", rel));
      }
      return builder.build();
   }

   protected static Link createLink(String path, String rel) {
      return Link.fromUri(createUri(path)).rel(rel).build();
   }

   protected static URI createUri(String path) {
      URI uri;
      try {
         uri = new URI("http://localhost.tck:888/url404/" + path);
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
      return uri;
   }

   @GET
   @Path("entitybodyresponsetest")
   public Response entityResponseTest() {
      RuntimeDelegate rd = RuntimeDelegate.getInstance();
      Response.ResponseBuilder rb = rd.createResponseBuilder();
      String rwe = "hello";
      Response build = rb.entity(rwe).build();
      return build;
   }

   @GET
   @Path("nullEntityResponse")
   public Response nullEntityResponse() {
      RuntimeDelegate rd = RuntimeDelegate.getInstance();
      Response.ResponseBuilder rb = rd.createResponseBuilder();
      return rb.entity((Object)null).build();
   }
}
