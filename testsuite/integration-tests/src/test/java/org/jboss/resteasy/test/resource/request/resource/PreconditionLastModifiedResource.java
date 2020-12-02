package org.jboss.resteasy.test.resource.request.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

@Path("/")
public class PreconditionLastModifiedResource {

   @GET
   public Response doGet(@Context Request request) {
      GregorianCalendar lastModified = new GregorianCalendar(2007, 0, 0, 0, 0, 0);
      Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified.getTime());
      if (rb != null) {
         return rb.build();
      }

      return Response.ok("foo", "text/plain").build();
   }

   @Path("millis")
   @GET
   public Response doGetWithMillis(@Context Request request) {
      final Calendar lastModified = new Calendar.Builder()
          .setDate(2020, Calendar.DECEMBER, 11)
          .setTimeOfDay(22, 47, 15, 999)
          .setTimeZone(TimeZone.getTimeZone("GMT"))
          .build();
      final Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(lastModified.getTime());
      if (responseBuilder == null) {
         // Last modified date didn't match, send new content
         return Response.ok("new content", "text/plain")
             .lastModified(lastModified.getTime())
             .build();
      }
      // Sending 304 not modified
      return responseBuilder.build();
   }
}
