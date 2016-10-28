package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/float/{arg}")
public class UriParamAsPrimitiveResourceUriFloat {
    @GET
    public String doGet(@PathParam("arg") float v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 3.14159265f, v, 0.0f);
        return "content";
    }
}
