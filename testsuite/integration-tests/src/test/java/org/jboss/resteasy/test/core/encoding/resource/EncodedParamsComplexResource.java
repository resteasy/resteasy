package org.jboss.resteasy.test.core.encoding.resource;

import org.jboss.resteasy.test.core.encoding.EncodedParamsTest;
import org.junit.Assert;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/encodedParam")
public class EncodedParamsComplexResource {
    @GET
    public String get(@QueryParam("hello world") int num, @QueryParam("stuff") @Encoded String stuff,
                      @QueryParam("stuff") String unStuff) {
        Assert.assertEquals(EncodedParamsTest.ERROR_MESSAGE, 5, num);
        Assert.assertEquals(EncodedParamsTest.ERROR_MESSAGE, "hello%20world", stuff);
        Assert.assertEquals(EncodedParamsTest.ERROR_MESSAGE, "hello world", unStuff);
        return "HELLO";
    }

    @GET
    @Path("/{param}")
    public String goodbye(@PathParam("param") @Encoded String stuff) {
        Assert.assertEquals(EncodedParamsTest.ERROR_MESSAGE, "hello%20world", stuff);
        return "GOODBYE";
    }
}
