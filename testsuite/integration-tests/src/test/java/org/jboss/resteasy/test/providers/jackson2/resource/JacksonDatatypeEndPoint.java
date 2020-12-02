package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Path("/")
public class JacksonDatatypeEndPoint {

   @GET
   @Path("/string")
   @Produces(MediaType.APPLICATION_JSON)
   public String getString() {
      return "someString";
   }

   @GET
   @Path("/date")
   @Produces(MediaType.APPLICATION_JSON)
   public Date getDate() {
      return new Date();
   }

   @GET
   @Path("/duration")
   @Produces(MediaType.APPLICATION_JSON)
   public Duration getDuration() {
      return Duration.ofSeconds(5, 6);
   }

   @GET
   @Path("/optional/{nullParam}")
   @Produces(MediaType.APPLICATION_JSON)
   public Optional<String> getOptional(@PathParam("nullParam") boolean nullParameter) {
      return nullParameter ? Optional.<String>empty() : Optional.of("info@example.com");
   }
}
