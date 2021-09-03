package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public class ReaderWriterSub {
   @Path("/without")
   @GET
   @Produces("text/plain")
   public String get() {
      return "hello";
   }
}
