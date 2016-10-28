package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Singleton
public class SingletonSubResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "hello";
    }
}
