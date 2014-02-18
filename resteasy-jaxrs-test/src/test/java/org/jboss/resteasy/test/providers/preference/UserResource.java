package org.jboss.resteasy.test.providers.preference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/user")
@Produces("text/plain")
public class UserResource {

    private static final User user = new User("jharting", "email@example.com");

    @GET
    public User getUser() {
        return user;
    }
}
