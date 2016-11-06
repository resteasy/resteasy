package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class JettisonMediaTypeService {
    @GET
    @Path("bug")
    @Produces({"application/json;charset=UTF-8"})
    public JettisonMediaTypeObject bug() {
        return new JettisonMediaTypeObject();
    }

    @GET
    @Path("nobug")
    @Produces({"application/json"})
    public JettisonMediaTypeObject nobug() {
        return new JettisonMediaTypeObject();
    }
}
