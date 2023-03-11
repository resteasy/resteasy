package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

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

    @POST
    @Produces("application/byte")
    public String doPostByte(@QueryParam("byte") byte[] v) {
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

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") char[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v[0]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v[1]);
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v[2]);
        return "content";
    }
}
