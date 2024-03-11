package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/default/null")
public class MatrixParamAsPrimitiveDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") boolean v) {
        Assertions.assertEquals(false, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") byte v) {
        Assertions.assertTrue(0 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") short v) {
        Assertions.assertTrue(0 == v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") int v) {
        Assertions.assertEquals(0, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") long v) {
        Assertions.assertEquals(0L, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") float v) {
        Assertions.assertEquals(0.0f, v, 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") double v) {
        Assertions.assertEquals(0.0d, v, 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") char v) {
        Assertions.assertEquals(Character.MIN_VALUE, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
