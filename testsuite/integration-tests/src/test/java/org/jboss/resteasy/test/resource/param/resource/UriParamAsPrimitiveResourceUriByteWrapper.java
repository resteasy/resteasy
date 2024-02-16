package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/byte/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriByteWrapper {
    @GET
    public String doGet(@PathParam("arg") Byte v) {
        Assertions.assertTrue(127 == v.byteValue(), UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
