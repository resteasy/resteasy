package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.test.resource.param.UriParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/byte/wrapper/{arg}")
public class UriParamAsPrimitiveResourceUriByteWrapper {
    @GET
    public String doGet(@PathParam("arg") Byte v) {
        Assert.assertTrue(UriParamAsPrimitiveTest.ERROR_CODE, 127 == v.byteValue());
        return "content";
    }
}
