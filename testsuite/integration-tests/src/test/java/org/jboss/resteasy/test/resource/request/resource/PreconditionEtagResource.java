package org.jboss.resteasy.test.resource.request.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

@Path("/etag")
public class PreconditionEtagResource {

   @GET
   public Response doGet(@Context Request request) {
      Response.ResponseBuilder rb = request.evaluatePreconditions(new EntityTag("1"));
      if (rb != null) {
         return rb.build();
      }

      return Response.ok("foo", "text/plain").build();
   }

   @Context
   Request myRequest;

   @GET
   @Path("/fromField")
   public Response doGet() {
      Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1"));
      if (rb != null) {
         return rb.build();
      }

      return Response.ok("foo", "text/plain").build();
   }

   @GET
   @Path("/weak")
   public Response GetWeak() {
      Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1", true));
      if (rb != null) {
         return rb.build();
      }

      return Response.ok("foo", "text/plain").build();
   }

}
