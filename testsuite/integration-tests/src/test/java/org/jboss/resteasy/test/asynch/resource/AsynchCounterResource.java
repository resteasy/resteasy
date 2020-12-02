package org.jboss.resteasy.test.asynch.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class AsynchCounterResource {

   @GET
   public String get() throws Exception {
      Thread.sleep(1500);
      return "get";
   }
}
