package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/int/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriIntWrapper {
    @GET
    public String doGet(@PathParam("arg") Integer v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 2147483647, v.intValue());
        return "content";
    }
}
