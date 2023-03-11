package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/wrappers/default/override")
public class MatrixParamAsPrimitiveWrappersDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") @DefaultValue("false") Boolean v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, true, v.booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") @DefaultValue("1") Byte v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v.byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") @DefaultValue("1") Short v) {
        Assert.assertTrue(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v.shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") @DefaultValue("1") Integer v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") @DefaultValue("1") Long v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") @DefaultValue("0.0") Float v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") @DefaultValue("0.0") Double v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.doubleValue(), 0.0);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") @DefaultValue("b") Character v) {
        Assert.assertEquals(MatrixParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v.charValue());
        return "content";
    }
}
