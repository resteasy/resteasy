package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("users/{userID}/certs")
public class ResourceMatchingMultipleUserCertResource {
    @GET
    public String findUserCerts(
            @PathParam("userID") String userID) {
        return "users/{id}/certs " + userID;

    }
}
