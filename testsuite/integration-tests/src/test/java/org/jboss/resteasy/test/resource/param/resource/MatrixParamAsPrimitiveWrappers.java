package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/wrappers")
public class MatrixParamAsPrimitiveWrappers {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") Boolean v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v.booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") Byte v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") Short v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v.shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") Integer v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") Long v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") Float v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") Double v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.doubleValue(), 0.0);
        return "content";
    }
}
