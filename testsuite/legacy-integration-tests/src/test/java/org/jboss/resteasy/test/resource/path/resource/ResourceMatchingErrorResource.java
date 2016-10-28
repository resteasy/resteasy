package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("error")
public class ResourceMatchingErrorResource {
    @GET
    @Produces("text/*")
    public String test() {
        return getClass().getSimpleName();
    }

    @POST
    @Produces("text/*")
    public Response response(String msg) {
        return Response.ok(msg).build();
    }
}
