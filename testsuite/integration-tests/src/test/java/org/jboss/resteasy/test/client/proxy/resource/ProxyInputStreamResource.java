package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class ProxyInputStreamResource {
    @GET
    @Produces("text/plain")
    public String get() {
        return "hello world";
    }

}
