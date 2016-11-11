package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.SortedSet;

@Path("/sortedset")
public interface HeaderParamsAsPrimitivesSortedSetProxy {
    @GET
    @Produces("application/boolean")
    String doGetBoolean(@HeaderParam("header") SortedSet<String> v);
}
