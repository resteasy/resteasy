package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/short/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriShortWrapper {
    @GET
    public String doGet(@PathParam("arg") Short v) {
        Assertions.assertTrue(32767 == v.shortValue(), UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
