package org.jboss.resteasy.test.core.basic.resource;

import javax.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

@Path("/test")
public class PrivateConstructorServiceResource {
   PrivateConstructorServiceResource() {

   }

   public PrivateConstructorServiceResource(@Context final javax.servlet.ServletContext context, @Context final HttpServletRequest request) {

   }

   @GET
   @Produces("text/plain")
   public String get() {
      return "hello";
   }
}
