package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/short/{arg}")
public class UriParamAsPrimitiveResourceUriShort {
    @GET
    public String doGet(@PathParam("arg") short v) {
        Assertions.assertTrue(32767 == v, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
