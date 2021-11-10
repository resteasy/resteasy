package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Set;

@Path("/set")
public interface HeaderParamsAsPrimitivesSetProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean(@HeaderParam("header") Set<String> v);
}
