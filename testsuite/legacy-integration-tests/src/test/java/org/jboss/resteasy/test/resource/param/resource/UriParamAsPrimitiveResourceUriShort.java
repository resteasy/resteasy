package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/short/{arg}")
public class UriParamAsPrimitiveResourceUriShort {
    @GET
    public String doGet(@PathParam("arg") short v) {
        Assert.assertTrue(UriParamAsPrimitiveTest.ERROR_CODE, 32767 == v);
        return "content";
    }
}
