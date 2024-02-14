package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default/null")
public class MatrixParamAsPrimitiveWrappersDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@MatrixParam("boolean") Boolean v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@MatrixParam("byte") Byte v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@MatrixParam("short") Short v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@MatrixParam("int") Integer v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@MatrixParam("long") Long v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@MatrixParam("float") Float v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@MatrixParam("double") Double v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@MatrixParam("char") Character v) {
        Assertions.assertEquals(null, v, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
