package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;

@Path("/test")
public interface ProxyInputStreamProxy {
    @GET
    @Produces("text/plain")
    InputStream get();

}
