package org.jboss.resteasy.test.core.basic.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

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
