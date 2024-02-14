package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/float/{arg}")
public class UriParamAsPrimitiveResourceUriFloat {
    @GET
    public String doGet(@PathParam("arg") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
