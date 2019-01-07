package org.jboss.resteasy.test.security.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ssl")
public class SslResource {

   @Path("/hello")
   @GET
   public String hello() {
      return "Hello World!";
   }
}
