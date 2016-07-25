package org.jboss.resteasy.test.core.smoke.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface ResourceWithMultipleInterfacesIntA {
    @GET
    @Path("foo")
    @Produces("text/plain")
    String getFoo();
}
