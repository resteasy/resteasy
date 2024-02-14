package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/long/{arg}")
public class UriParamAsPrimitiveResourceUriLong {
    @GET
    public String doGet(@PathParam("arg") long v) {
        Assertions.assertEquals(9223372036854775807L, v, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
