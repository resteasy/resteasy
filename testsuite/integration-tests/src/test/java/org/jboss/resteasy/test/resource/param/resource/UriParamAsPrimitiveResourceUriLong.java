package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/long/{arg}")
public class UriParamAsPrimitiveResourceUriLong {
    @GET
    public String doGet(@PathParam("arg") long v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 9223372036854775807L, v);
        return "content";
    }
}
