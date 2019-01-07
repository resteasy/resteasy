package org.jboss.resteasy.test.security.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/secure")
public class HostnameVerificationPolicyResource {

   @Path("/hello")
   @GET
   public String hello() {
      return "Hello World!";
   }
}
