package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("happiness")
public class PrivateNoArgConstructorResource {
    @GET
    public Response searchForHappiness(@BeanParam HappinessParams searchParams) {
        return Response.ok("successful call").build();
    }
}
