package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class MatrixParamAsPrimitivePrimitives {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") boolean v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") byte v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") short v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") int v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") long v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") float v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") double v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v, 0.0);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") char v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v);
        return "content";
    }
}
