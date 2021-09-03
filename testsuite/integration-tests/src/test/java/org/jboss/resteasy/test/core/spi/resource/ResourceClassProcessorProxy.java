package org.jboss.resteasy.test.core.spi.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/proxy")
public interface ResourceClassProcessorProxy {
   @POST // should be replaced by GET in ResourceClassProcessorMethod
   String custom();
}
