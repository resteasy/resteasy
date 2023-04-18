package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("sub")
public class InheritedContextNewService extends InheritedContextService {
    @Path("test/{level}")
    @GET
    public String test(@PathParam("level") String level) {
        return Boolean.toString(level.equals("SomeService") && testContexts());
    }
}
