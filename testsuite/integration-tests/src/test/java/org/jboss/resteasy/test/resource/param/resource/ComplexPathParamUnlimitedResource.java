package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.jupiter.api.Assertions;

@Path("/unlimited")
public class ComplexPathParamUnlimitedResource {
    @Path("{1}-{rest:.*}")
    @GET
    public String get(@PathParam("1") int one, @PathParam("rest") String rest) {
        Assertions.assertEquals(1, one, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals("on/and/on", rest, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        return "ok";
    }
}
