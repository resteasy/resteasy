package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;

@Path(value = "/PathParamTest")
public class LocatorWithClassHierarchyPathParamResource {

    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/{id}/{id1}")
    public String two(@PathParam("id") String id,
                      @PathParam("id1") PathSegment id1) {
        return "double=" + id + id1.getPath();
    }

    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("/ParamEntityWithConstructor/{id}")
    public String paramEntityWithConstructorTest(
            @DefaultValue("PathParamTest") @PathParam("id") LocatorWithClassHierarchyParamEntityWithConstructor paramEntityWithConstructor) {
        return paramEntityWithConstructor.getValue();
    }
}
