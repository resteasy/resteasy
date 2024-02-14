package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class MatrixParamAsPrimitivePrimitives {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") boolean v) {
        Assertions.assertEquals(true, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") byte v) {
        Assertions.assertTrue((byte) 127 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") short v) {
        Assertions.assertTrue((short) 32767 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") int v) {
        Assertions.assertEquals(2147483647, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") long v) {
        Assertions.assertEquals(9223372036854775807L, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") char v) {
        Assertions.assertEquals('a', v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
