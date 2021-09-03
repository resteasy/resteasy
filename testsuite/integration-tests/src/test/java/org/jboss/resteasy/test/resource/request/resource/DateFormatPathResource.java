package org.jboss.resteasy.test.resource.request.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/")
public class DateFormatPathResource {
   @Path("/widget/{date}")
   @GET
   @Produces("text/plain")
   public String get(@PathParam("date") String date) {
      return date;
   }
}
