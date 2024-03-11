package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/double/{arg}")
public class UriParamAsPrimitiveResourceUriDouble {
    @GET
    public String doGet(@PathParam("arg") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
