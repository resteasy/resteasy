package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/array")
public class QueryParamAsPrimitiveResourceArray implements QueryParamAsPrimitiveResourceResourceArray {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") boolean[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v[0]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v[1]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v[2]);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") byte[] v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[0]);
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[1]);
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[2]);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") short[] v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v[0]);
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v[1]);
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v[2]);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") int[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[0]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[1]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[2]);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") long[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[0]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[1]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[2]);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") float[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[0], 0.0f);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[1], 0.0f);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[2], 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") double[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[0], 0.0);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[1], 0.0);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[2], 0.0);
        return "content";
    }
}
