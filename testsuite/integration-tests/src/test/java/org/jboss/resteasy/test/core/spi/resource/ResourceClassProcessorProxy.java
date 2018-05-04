package org.jboss.resteasy.test.core.spi.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/proxy")
public interface ResourceClassProcessorProxy {
    @GET
    String custom();
}
