package org.jboss.resteasy.test.core.basic.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public interface ProviderInjectionSimpleResource {
   @GET
   @Produces("text/plain")
   String foo();
}
