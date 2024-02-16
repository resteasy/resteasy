package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/boolean/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriBooleanWrapper {
    @GET
    public String doGet(@PathParam("arg") Boolean v) {
        Assertions.assertEquals(true, v.booleanValue(), UriParamAsPrimitiveTest.ERROR_CODE);
        return "content";
    }
}
