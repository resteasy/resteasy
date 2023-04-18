package org.jboss.resteasy.test.cdi.injection.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("user")
public class UserResource {

    @Inject
    private UserManager userManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationUser getUser() {
        return userManager.getUser();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ApplicationUser getUserJaxb() {
        return userManager.getUser();
    }

}
