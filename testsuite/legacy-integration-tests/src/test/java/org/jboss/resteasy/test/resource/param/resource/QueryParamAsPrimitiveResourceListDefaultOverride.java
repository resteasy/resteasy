package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/list/default/override")
public class QueryParamAsPrimitiveResourceListDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") @DefaultValue("false") List<Boolean> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v.get(0).booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") @DefaultValue("0") List<Byte> v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.get(0).byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") @DefaultValue("0") List<Short> v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v.get(0).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") @DefaultValue("0") List<Integer> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.get(0).intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") @DefaultValue("0") List<Long> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.get(0).longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") @DefaultValue("0.0") List<Float> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.get(0).floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") @DefaultValue("0.0") List<Double> v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.get(0).doubleValue(), 0.0);
        return "content";
    }
}
