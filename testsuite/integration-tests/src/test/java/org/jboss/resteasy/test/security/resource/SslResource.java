package org.jboss.resteasy.test.security.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ssl")
public class SslResource {

   @Path("/hello")
   @GET
   public String hello() {
      return "Hello World!";
   }
}
