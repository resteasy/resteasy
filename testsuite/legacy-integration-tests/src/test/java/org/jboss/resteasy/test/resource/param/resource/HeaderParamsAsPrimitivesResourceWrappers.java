package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/wrappers")
public class HeaderParamsAsPrimitivesResourceWrappers implements HeaderParamsAsPrimitivesWrappersProxy {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") Boolean v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v.booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") Byte v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v.byteValue());

        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") Short v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v.shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") Integer v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v.intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") Long v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v.longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") Float v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v.floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") Double v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v.doubleValue(), 0.0);
        return "content";
    }
}
