package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.io.InputStream;

@Path("/test")
public interface ProxyInputStreamProxy {
   @GET
   @Produces("text/plain")
   InputStream get();

}
