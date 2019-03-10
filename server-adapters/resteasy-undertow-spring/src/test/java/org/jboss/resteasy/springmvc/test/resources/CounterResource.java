package org.jboss.resteasy.springmvc.test.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/count")
public class CounterResource {
   private int count;

   @GET
   @Produces("text/plain")
   public Integer getCount() {
      return ++count;
   }
}
