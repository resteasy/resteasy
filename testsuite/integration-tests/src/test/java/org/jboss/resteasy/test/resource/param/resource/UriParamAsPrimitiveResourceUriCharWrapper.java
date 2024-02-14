package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/char/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriCharWrapper {
    @GET
    public String doGet(@PathParam("arg") Character v) {
        Assertions.assertEquals('a', v.charValue(), UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
