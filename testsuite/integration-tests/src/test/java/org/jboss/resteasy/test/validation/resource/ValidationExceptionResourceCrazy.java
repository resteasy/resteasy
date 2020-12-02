package org.jboss.resteasy.test.validation.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
@ValidationExceptionCrazyConstraint
public class ValidationExceptionResourceCrazy {
   private String s;

   @GET
   public String test() {
      return s;
   }
}
