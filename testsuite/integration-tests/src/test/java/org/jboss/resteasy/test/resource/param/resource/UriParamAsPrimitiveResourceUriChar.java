package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/char/{arg}")
public class UriParamAsPrimitiveResourceUriChar {
    @GET
    public String doGet(@PathParam("arg") char v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 'a', v);
        return "content";
    }
}
