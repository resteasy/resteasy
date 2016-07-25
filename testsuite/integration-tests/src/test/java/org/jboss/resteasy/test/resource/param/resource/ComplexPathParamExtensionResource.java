package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.ComplexPathParamTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public class ComplexPathParamExtensionResource {
    @GET
    @Path("/{1},{2}/{3}/blah{4}-{5}ttt")
    public String get(@PathParam("1") int one, @PathParam("2") int two, @PathParam("3") int three,
                      @PathParam("4") int four, @PathParam("5") int five) {
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, one, 1);
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, two, 2);
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, three, 3);
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, four, 4);
        Assert.assertEquals(ComplexPathParamTest.WRONG_REQUEST_ERROR_MESSAGE, five, 5);
        return "hello";
    }

}
