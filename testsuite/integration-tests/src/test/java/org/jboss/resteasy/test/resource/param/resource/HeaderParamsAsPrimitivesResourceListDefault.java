package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/list/default")
public class HeaderParamsAsPrimitivesResourceListDefault {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("true") List<Boolean> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v.get(0).booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@HeaderParam("byte") @DefaultValue("127") List<Byte> v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v.get(0).byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") @DefaultValue("32767") List<Short> v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v.get(0).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@HeaderParam("int") @DefaultValue("2147483647") List<Integer> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v.get(0).intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@HeaderParam("long") @DefaultValue("9223372036854775807") List<Long> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v.get(0).longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@HeaderParam("float") @DefaultValue("3.14159265") List<Float> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v.get(0).floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@HeaderParam("double") @DefaultValue("3.14159265358979") List<Double> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v.get(0).doubleValue(), 0.0);
        return "content";
    }
}
