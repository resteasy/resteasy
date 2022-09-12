package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("")
public class CustomValueInjectorHelloResource {
   @GET
   @Produces("text/plain")
   public String get(@CustomValueInjectorHello("world") String hello) {
      return hello;
   }
}
