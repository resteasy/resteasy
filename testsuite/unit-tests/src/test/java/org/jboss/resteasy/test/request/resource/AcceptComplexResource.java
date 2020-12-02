package org.jboss.resteasy.test.request.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class AcceptComplexResource {
   @Consumes("text/*")
   @Produces("text/html")
   @GET
   public String method1() {
      return null;
   }

   @Consumes("text/xml")
   @Produces("text/json")
   @GET
   public String method2() {
      return null;
   }
}
