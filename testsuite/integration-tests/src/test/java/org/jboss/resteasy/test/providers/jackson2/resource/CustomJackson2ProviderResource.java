package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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
