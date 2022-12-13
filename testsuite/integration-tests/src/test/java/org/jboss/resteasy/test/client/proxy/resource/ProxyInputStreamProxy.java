package org.jboss.resteasy.test.client.proxy.resource;

import java.io.InputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public interface ProxyInputStreamProxy {
    @GET
    @Produces("text/plain")
    InputStream get();

}
