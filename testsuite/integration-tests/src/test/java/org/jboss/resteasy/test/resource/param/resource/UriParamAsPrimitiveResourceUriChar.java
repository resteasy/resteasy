package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/char/{arg}")
public class UriParamAsPrimitiveResourceUriChar {
    @GET
    public String doGet(@PathParam("arg") char v) {
        Assertions.assertEquals('a', v, UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
