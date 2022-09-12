package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class InterceptorStreamResource {
   @POST
   @Path("test")
   public String createBook(String test) {
      return test;
   }
}
