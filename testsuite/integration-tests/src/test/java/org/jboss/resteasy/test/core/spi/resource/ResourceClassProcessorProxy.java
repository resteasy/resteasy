package org.jboss.resteasy.test.core.spi.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/proxy")
public interface ResourceClassProcessorProxy {
    @POST // should be replaced by GET in ResourceClassProcessorMethod
    String custom();
}
