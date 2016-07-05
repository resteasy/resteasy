package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/double/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriDoubleWrapper {
    @GET
    public String doGet(@PathParam("arg") Double v) {
        Assert.assertEquals(UriParamAsPrimitiveTest.ERROR_CODE, 3.14159265358979d, v.doubleValue(), 0.0);
        return "content";
    }
}
