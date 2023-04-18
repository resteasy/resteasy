package org.jboss.resteasy.test.util.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/foo/")
public interface ObjectToURIResouce {
    @Path("{id}")
    @GET
    ObjectToURIAbstractURITemplateObject getFoo(@PathParam("id") Integer id);
}
