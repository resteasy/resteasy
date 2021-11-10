package org.jboss.resteasy.test.core.logging.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class DebugLoggingEndPoint {

   @POST
   @Path("custom")
   @Produces("aaa/bbb")
   @Consumes("aaa/bbb")
   public String custom(String data) {
      return data;
   }

   @POST
   @Path("build/in")
   public String buildIn(String data) {
      return data;
   }
}
