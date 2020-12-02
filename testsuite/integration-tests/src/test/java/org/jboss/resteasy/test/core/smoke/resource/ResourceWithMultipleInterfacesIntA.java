package org.jboss.resteasy.test.core.smoke.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ResourceWithMultipleInterfacesIntA {
   @GET
   @Path("foo")
   @Produces("text/plain")
   String getFoo();
}
