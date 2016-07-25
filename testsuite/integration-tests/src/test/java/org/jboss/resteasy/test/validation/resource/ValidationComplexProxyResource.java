package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("proxy")
public class ValidationComplexProxyResource implements ValidationComplexProxyInterface {
    private static String s;

    @GET
    @Produces("text/plain")
    @Size(min = 2, max = 4)
    public String g() {
        return s;
    }

    @POST
    @Path("{s}")
    public void s(@PathParam("s") String s) {
        ValidationComplexProxyResource.s = s;
    }
}
