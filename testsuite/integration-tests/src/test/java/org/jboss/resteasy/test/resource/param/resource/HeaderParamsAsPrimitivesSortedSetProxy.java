package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.SortedSet;

@Path("/sortedset")
public interface HeaderParamsAsPrimitivesSortedSetProxy {
   @GET
   @Produces("application/boolean")
   String doGetBoolean(@HeaderParam("header") SortedSet<String> v);
}
