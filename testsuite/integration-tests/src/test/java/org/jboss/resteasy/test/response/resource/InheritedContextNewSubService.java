package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("subsub")
public class InheritedContextNewSubService extends InheritedContextNewService {
    @Path("test/{level}")
    @GET
    public String test(@PathParam("level") String level) {
        return Boolean.toString(level.equals("SomeSubService") && testContexts());
    }
}
