package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/thePatron")
@ApplicationScoped
public class FollowRedirectsResource {

    @Path("redirected")
    @GET
    public Response redirected(){
        return Response.ok("OK").build();
    }


    @GET
    @Path("redirectedDirectResponse")
    public String redirectedDirectResponse() {
        return "ok - direct response";
    }
}
