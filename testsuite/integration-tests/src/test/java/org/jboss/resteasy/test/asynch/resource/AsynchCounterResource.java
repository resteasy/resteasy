package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class AsynchCounterResource {

   @GET
   public String get() throws Exception {
      Thread.sleep(1500);
      return "get";
   }
}
