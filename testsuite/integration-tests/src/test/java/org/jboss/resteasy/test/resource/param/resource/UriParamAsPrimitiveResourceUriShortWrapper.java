package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/short/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriShortWrapper {
    @GET
    public String doGet(@PathParam("arg") Short v) {
        Assert.assertTrue(UriParamAsPrimitiveTest.ERROR_CODE, 32767 == v.shortValue());
        return "content";
    }
}
