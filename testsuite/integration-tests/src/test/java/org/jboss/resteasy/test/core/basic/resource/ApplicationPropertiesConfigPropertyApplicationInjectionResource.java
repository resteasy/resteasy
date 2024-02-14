package org.jboss.resteasy.test.core.basic.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.junit.jupiter.api.Assertions;

@Path("/")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class ApplicationPropertiesConfigPropertyApplicationInjectionResource {

    @Inject
    private Application application;

    @GET
    @Path("/getconfigproperty")
    public Response getProperty(@QueryParam("prop") String prop) {
        String response = "false";
        boolean containskey = application.getProperties().containsKey(prop);
        if (containskey) {
            response = "true";
        }
        Assertions.assertEquals("true", response,
                "The injected application doesn't contain property \"Prop1\"");
        String value = (String) application.getProperties().get("Prop1");
        return Response.ok(value).build();
    }
}
