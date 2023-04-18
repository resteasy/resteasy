package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("users/{userID}/memberships")
public class ResourceMatchingMultipleUserMembershipResource {
    @GET
    public String findUserMemberships(
            @PathParam("userID") String userID) {
        return "users/{id}/memberships " + userID;
    }
}
