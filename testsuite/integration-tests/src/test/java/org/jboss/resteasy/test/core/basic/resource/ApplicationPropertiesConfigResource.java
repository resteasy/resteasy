package org.jboss.resteasy.test.core.basic.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Configuration;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("/")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class ApplicationPropertiesConfigResource {

    @Inject
    private Configuration configuration;

    @GET
    @Path("/getconfigproperty")
    public String getProperty(@QueryParam("prop") String prop) {
        String value = (String) configuration.getProperty(prop);
        return value;
    }
}
