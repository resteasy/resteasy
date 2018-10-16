package org.jboss.resteasy.test.core.basic.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public interface ProviderInjectionSimpleResource {
   @GET
   @Produces("text/plain")
   String foo();
}
