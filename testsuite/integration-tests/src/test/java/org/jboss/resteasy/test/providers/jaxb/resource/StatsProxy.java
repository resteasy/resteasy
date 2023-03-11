package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.plugins.stats.RegistryData;

@Path("/resteasy/registry")
public interface StatsProxy {
    @GET
    @Produces("application/xml")
    RegistryData get();
}
