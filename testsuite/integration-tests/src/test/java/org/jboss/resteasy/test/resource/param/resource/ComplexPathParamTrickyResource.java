package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.jupiter.api.Assertions;

@Path("/tricky")
public class ComplexPathParamTrickyResource {
    @GET
    @Path("{hello}")
    public String getHello(@PathParam("hello") int one) {
        Assertions.assertEquals(one, 1, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        return "hello";
    }

    @GET
    @Path("{1},{2}")
    public String get2Groups(@PathParam("1") int one, @PathParam("2") int two) {
        Assertions.assertEquals(1, one, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals(2, two, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        return "2Groups";
    }

    @GET
    @Path("h{1}")
    public String getPrefixed(@PathParam("1") int one) {
        Assertions.assertEquals(1, one, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        return "prefixed";
    }
}
