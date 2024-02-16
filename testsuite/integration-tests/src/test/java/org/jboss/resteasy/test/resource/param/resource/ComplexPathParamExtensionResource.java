package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class ComplexPathParamExtensionResource {
    @GET
    @Path("/{1},{2}/{3}/blah{4}-{5}ttt")
    public String get(@PathParam("1") int one, @PathParam("2") int two, @PathParam("3") int three,
            @PathParam("4") int four, @PathParam("5") int five) {
        Assertions.assertEquals(one, 1, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals(two, 2, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals(three, 3, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals(four, 4, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        Assertions.assertEquals(five, 5, ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE);
        return "hello";
    }

}
