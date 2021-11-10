package org.jboss.resteasy.test.providers.jackson2.whitelist;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.TestPolymorphicType;

/**
 * @author bmaxwell
 */
@Path("/test")
public class TestRESTService {
    @POST
    @Path("/post")
    @Consumes("application/json")
    public Response postTest(TestPolymorphicType test) {
        return Response.status(HttpResponseCodes.SC_CREATED).entity("Test success: " + test).build();
    }
}