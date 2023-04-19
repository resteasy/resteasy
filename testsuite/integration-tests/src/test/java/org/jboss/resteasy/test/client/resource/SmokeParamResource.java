package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/foo")
public class SmokeParamResource {

    @POST
    @Produces("text/plain")
    @Consumes("text/plain")
    public String create(String cust) {
        return cust;
    }

    @GET
    @Produces("text/plain")
    public String get(@HeaderParam("a") String a, @QueryParam("b") String b) {
        return a + " " + b;
    }

}
