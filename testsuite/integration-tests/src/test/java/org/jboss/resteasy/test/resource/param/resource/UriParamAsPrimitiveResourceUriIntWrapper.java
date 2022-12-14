package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/int/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriIntWrapper {
    @GET
    public String doGet(@PathParam("arg") Integer v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 2147483647, v.intValue());
        return "content";
    }
}
