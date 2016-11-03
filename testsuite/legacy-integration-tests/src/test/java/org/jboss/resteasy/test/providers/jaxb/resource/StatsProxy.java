package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.plugins.stats.RegistryData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/resteasy/registry")
public interface StatsProxy {
    @GET
    @Produces("application/xml")
    RegistryData get();
}
