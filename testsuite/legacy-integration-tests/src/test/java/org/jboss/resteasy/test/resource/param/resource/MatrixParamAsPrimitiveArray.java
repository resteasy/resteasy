package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/array")
public class MatrixParamAsPrimitiveArray {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@MatrixParam("boolean") boolean[] v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v[0]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v[1]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v[2]);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@MatrixParam("byte") byte[] v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[0]);
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[1]);
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v[2]);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@MatrixParam("short") short[] v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 32767 == v[0]);
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 32767 == v[1]);
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 32767 == v[2]);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@MatrixParam("int") int[] v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[0]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[1]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v[2]);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@MatrixParam("long") long[] v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[0]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[1]);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v[2]);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@MatrixParam("float") float[] v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[0], 0.0f);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[1], 0.0f);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v[2], 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@MatrixParam("double") double[] v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[0], 0.0);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[1], 0.0);
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v[2], 0.0);
        return "content";
    }
}
