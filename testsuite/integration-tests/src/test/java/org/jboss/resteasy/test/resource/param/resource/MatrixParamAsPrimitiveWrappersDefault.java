package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default")
public class MatrixParamAsPrimitiveWrappersDefault {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") @DefaultValue("true") Boolean v) {
        Assertions.assertEquals(true, v.booleanValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") @DefaultValue("127") Byte v) {
        Assertions.assertTrue((byte) 127 == v.byteValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") @DefaultValue("32767") Short v) {
        Assertions.assertTrue((short) 32767 == v.shortValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") @DefaultValue("2147483647") Integer v) {
        Assertions.assertEquals(2147483647, v.intValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") @DefaultValue("9223372036854775807") Long v) {
        Assertions.assertEquals(9223372036854775807L, v.longValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") @DefaultValue("3.14159265") Float v) {
        Assertions.assertEquals(3.14159265f, v.floatValue(), 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") @DefaultValue("3.14159265358979") Double v) {
        Assertions.assertEquals(3.14159265358979d, v.doubleValue(), 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") @DefaultValue("a") Character v) {
        Assertions.assertEquals('a', v.charValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
