package org.jboss.resteasy.test.util.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/foo/")
public interface ObjectToURIResouce {
   @Path("{id}")
   @GET
   ObjectToURIAbstractURITemplateObject getFoo(@PathParam("id") Integer id);
}
