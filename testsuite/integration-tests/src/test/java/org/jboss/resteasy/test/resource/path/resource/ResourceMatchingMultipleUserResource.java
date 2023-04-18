package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("users")
public class ResourceMatchingMultipleUserResource {
    @GET
    @Path("{userID}")
    public String getUser(@PathParam("userID") String userID) {
        return "users/{id} " + userID;
    }
}
