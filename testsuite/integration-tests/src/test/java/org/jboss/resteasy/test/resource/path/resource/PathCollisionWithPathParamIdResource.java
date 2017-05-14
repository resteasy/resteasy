package org.jboss.resteasy.test.resource.path.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public class PathCollisionWithPathParamIdResource {

    @GET
    @Path("/list")
    public String getList() {
        return "/list";
    }

    @GET
    @Path("/{id}")
    public String getId(@PathParam("id") long myId) {
        return String.valueOf(myId);
    }
}
