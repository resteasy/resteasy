package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/long/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriLongWrapper {
    @GET
    public String doGet(@PathParam("arg") Long v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 9223372036854775807L, v.longValue());
        return "content";
    }
}
