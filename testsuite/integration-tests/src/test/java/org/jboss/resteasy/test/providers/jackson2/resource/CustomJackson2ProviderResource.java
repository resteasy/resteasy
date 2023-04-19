package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

@Path("/")
public class CustomJackson2ProviderResource {

    @GET
    @Produces("text/plain")
    @Path("/jackson2providerpath")
    public String getProviderPath() {
        return ResteasyJackson2Provider.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}
