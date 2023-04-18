package org.jboss.resteasy.test.contextProxyInterfaces.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HeaderValueProcessor;

@Path("/config")
public class CastableConfigurationResource {
    @Context
    Configuration config;

    @GET
    public Response getConfigurationClassName() {
        Response.ResponseBuilder builder = Response.ok(config.toString());
        if (config instanceof HeaderValueProcessor) {
            builder.header("Instanceof-HeaderValueProcessor", "true");
        } else {
            builder.header("Instanceof-HeaderValueProcessor", "false");
        }

        return builder.build();
    }
}
