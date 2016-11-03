package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/byte/{arg}")
public class UriParamAsPrimitiveResourceUriByte {
    @GET
    public String doGet(@PathParam("arg") byte v) {
        Assert.assertTrue(UriParamAsPrimitiveTest.ERROR_CODE, 127 == v);
        return "content";
    }
}
