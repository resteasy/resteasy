package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class CustomValueInjectorHelloResource {
    @GET
    @Produces("text/plain")
    public String get(@CustomValueInjectorHello("world") String hello) {
        return hello;
    }
}
