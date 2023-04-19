package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.plugins.stats.RegistryData;

@Path("/resteasy/registry")
public interface StatsProxy {
    @GET
    @Produces("application/xml")
    RegistryData get();
}
