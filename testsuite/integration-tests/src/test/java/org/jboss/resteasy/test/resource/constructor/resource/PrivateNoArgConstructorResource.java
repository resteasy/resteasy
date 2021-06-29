package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("happiness")
public class PrivateNoArgConstructorResource {
    @GET
    public Response searchForHappiness(@BeanParam HappinessParams searchParams)
    {
        return Response.ok("successful call").build();
    }
}
