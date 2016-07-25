package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/unlimited")
public class ComplexPathParamUnlimitedResource {
    @Path("{1}-{rest:.*}")
    @GET
    public String get(@PathParam("1") int one, @PathParam("rest") String rest) {
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, 1, one);
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, "on/and/on", rest);
        return "ok";
    }
}
