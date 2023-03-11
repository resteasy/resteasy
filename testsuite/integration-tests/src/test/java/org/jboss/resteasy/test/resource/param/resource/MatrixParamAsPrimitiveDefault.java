package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/default")
public class MatrixParamAsPrimitiveDefault {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") @DefaultValue("true") boolean v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") @DefaultValue("127") byte v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") @DefaultValue("32767") short v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") @DefaultValue("2147483647") int v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") @DefaultValue("9223372036854775807") long v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") @DefaultValue("3.14159265") float v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") @DefaultValue("3.14159265358979") double v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v, 0.0);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") @DefaultValue("a") char v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v);
        return "content";
    }
}
