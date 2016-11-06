package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/user")
@Produces("text/plain")
public class CustomProviderPreferenceUserResource {

    private static final CustomProviderPreferenceUser user = new CustomProviderPreferenceUser("jharting", "email@example.com");

    @GET
    public CustomProviderPreferenceUser getUser() {
        return user;
    }
}
