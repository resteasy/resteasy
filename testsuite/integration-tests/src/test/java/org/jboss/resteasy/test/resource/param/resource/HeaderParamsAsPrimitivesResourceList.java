package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/list")
public class HeaderParamsAsPrimitivesResourceList implements HeaderParamsAsPrimitivesListProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") List<Boolean> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v.get(0).booleanValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v.get(1).booleanValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v.get(2).booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@HeaderParam("byte") List<Byte> v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v.get(0).byteValue());
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v.get(1).byteValue());
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v.get(2).byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") List<Short> v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v.get(0).shortValue());
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v.get(1).shortValue());
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v.get(2).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@HeaderParam("int") List<Integer> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v.get(0).intValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v.get(1).intValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v.get(2).intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@HeaderParam("long") List<Long> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v.get(0).longValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v.get(1).longValue());
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v.get(2).longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@HeaderParam("float") List<Float> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v.get(0).floatValue(), 0.0f);
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v.get(1).floatValue(), 0.0f);
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v.get(2).floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@HeaderParam("double") List<Double> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v.get(0).doubleValue(), 0.0);
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v.get(1).doubleValue(), 0.0);
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v.get(2).doubleValue(), 0.0);
        return "content";
    }
}
