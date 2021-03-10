package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.Status.UNSUPPORTED_MEDIA_TYPE;

@Path("")
public class ClosedResponseHandlingResource {
   @Path("/testNotAcceptable/406")
   @GET
   public Response errorNotAcceptable() {
      return Response.status(NOT_ACCEPTABLE).build();
   }

   @Path("/testNotAcceptable")
   @GET
   public String getNotAcceptable(@Context UriInfo uriInfo) {
      URI endpoint406 = UriBuilder.fromUri(uriInfo.getRequestUri()).path("406").build();
      return ClientBuilder.newClient().target(endpoint406).request().get(String.class);
   }

   @Path("/testNotSupportedTraced/415")
   @GET
   public Response errorNotFound() {
      return Response.status(UNSUPPORTED_MEDIA_TYPE).build();
   }

   @Path("/testNotSupportedTraced")
   @GET
   public String getNotSupportedTraced(@Context UriInfo uriInfo) {
      URI endpoint415 = UriBuilder.fromUri(uriInfo.getRequestUri()).path("415").build();
      try {
         return ClientBuilder.newClient().target(endpoint415).request().get(String.class);
      } catch(NotSupportedException e) {
         throw new ClosedResponseHandlingPleaseMapException(e.getResponse());
      }
   }
}
