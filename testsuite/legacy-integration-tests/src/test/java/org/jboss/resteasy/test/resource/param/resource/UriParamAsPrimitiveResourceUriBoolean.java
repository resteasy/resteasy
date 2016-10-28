package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/boolean/{arg}")
public class UriParamAsPrimitiveResourceUriBoolean {
    @GET
    public String doGet(@PathParam("arg") boolean v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, true, v);
        return "content";
    }
}
