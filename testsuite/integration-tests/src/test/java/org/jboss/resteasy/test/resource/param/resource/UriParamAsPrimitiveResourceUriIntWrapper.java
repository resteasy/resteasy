package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/int/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriIntWrapper {
    @GET
    public String doGet(@PathParam("arg") Integer v) {
        Assertions.assertEquals(2147483647, v.intValue(), UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
