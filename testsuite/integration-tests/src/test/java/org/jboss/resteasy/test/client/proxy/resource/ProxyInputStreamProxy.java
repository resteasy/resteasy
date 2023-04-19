package org.jboss.resteasy.test.client.proxy.resource;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public interface ProxyInputStreamProxy {
    @GET
    @Produces("text/plain")
    InputStream get();

}
