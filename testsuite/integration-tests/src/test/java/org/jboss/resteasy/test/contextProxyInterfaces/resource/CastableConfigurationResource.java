package org.jboss.resteasy.test.contextProxyInterfaces.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("/config")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class CastableConfigurationResource {
    @Inject
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
