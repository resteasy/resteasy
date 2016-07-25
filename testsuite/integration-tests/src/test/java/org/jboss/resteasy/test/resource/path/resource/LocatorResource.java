package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("resource")
public class LocatorResource {
    @GET
    @Path("responseok")
    public String responseOk() {
        return "ok";
    }

    @Path("{id}")
    public Object locate(@PathParam("id") int id) {
        return new LocatorTestLocator2();
    }
}
