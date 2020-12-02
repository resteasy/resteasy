package org.jboss.resteasy.springmvc.test.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/count")
public class CounterResource {
   private int count;

   @GET
   @Produces("text/plain")
   public Integer getCount() {
      return ++count;
   }
}
