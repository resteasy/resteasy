package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;

import static jakarta.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static jakarta.ws.rs.core.Response.Status.UNSUPPORTED_MEDIA_TYPE;

@Path("")
public class ClosedResponseHandlingResource {

   /**
    * Sets the System property ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR
    * @param value value property is set to
    */
   @GET
   @Path("behavior/{value}")
   public void setBehavior(@PathParam("value") String value) {
      System.setProperty(ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR, value);
   }

   @Path("/testNotAcceptable/406")
   @GET
   public Response errorNotAcceptable() {
      return Response.status(NOT_ACCEPTABLE).build();
   }

   @Path("/testNotAcceptable")
   @GET
   public String getNotAcceptable(@Context UriInfo uriInfo) {
      URI endpoint406 = UriBuilder.fromUri(uriInfo.getRequestUri()).path("406").build();
      Client client = ClientBuilder.newClient();
      try {
         return client.target(endpoint406).request().get(String.class);
      } finally {
         client.close();
      }
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
      Client client = ClientBuilder.newClient();
      try {
         return client.target(endpoint415).request().get(String.class);
      } catch(NotSupportedException e) {
         throw new ClosedResponseHandlingPleaseMapException(e.getResponse());
      } finally {
         client.close();
      }
   }
}
