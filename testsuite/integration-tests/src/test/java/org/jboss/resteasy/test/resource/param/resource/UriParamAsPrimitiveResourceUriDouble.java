package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/double/{arg}")
public class UriParamAsPrimitiveResourceUriDouble {
    @GET
    public String doGet(@PathParam("arg") double v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 3.14159265358979d, v, 0.0);
        return "content";
    }
}
