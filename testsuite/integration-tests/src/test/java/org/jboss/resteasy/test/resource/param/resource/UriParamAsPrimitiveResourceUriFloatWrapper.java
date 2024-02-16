package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/float/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriFloatWrapper {
    @GET
    public String doGet(@PathParam("arg") Float v) {
        Assertions.assertEquals(3.14159265f, v.floatValue(), 0.0f, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
