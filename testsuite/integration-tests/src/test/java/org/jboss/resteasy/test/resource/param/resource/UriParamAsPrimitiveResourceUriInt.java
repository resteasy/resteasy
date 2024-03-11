package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/int/{arg}")
public class UriParamAsPrimitiveResourceUriInt {
    @GET
    public String doGet(@PathParam("arg") int v) {
        Assertions.assertEquals(2147483647, v, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
