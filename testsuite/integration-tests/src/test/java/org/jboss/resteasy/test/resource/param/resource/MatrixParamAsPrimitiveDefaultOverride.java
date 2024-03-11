package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/default/override")
public class MatrixParamAsPrimitiveDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") @DefaultValue("false") boolean v) {
        Assertions.assertEquals(true, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") @DefaultValue("1") byte v) {
        Assertions.assertTrue((byte) 127 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") @DefaultValue("1") short v) {
        Assertions.assertTrue((short) 32767 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") @DefaultValue("1") int v) {
        Assertions.assertEquals(2147483647, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") @DefaultValue("1") long v) {
        Assertions.assertEquals(9223372036854775807L, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") @DefaultValue("0.0") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") @DefaultValue("0.0") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") @DefaultValue("b") char v) {
        Assertions.assertEquals('a', v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
