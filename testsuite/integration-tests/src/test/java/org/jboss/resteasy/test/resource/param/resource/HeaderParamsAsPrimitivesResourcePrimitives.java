package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class HeaderParamsAsPrimitivesResourcePrimitives implements HeaderParamsAsPrimitivesPrimitivesProxy {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") boolean v) {
        Assertions.assertEquals(true, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") byte v) {
        Assertions.assertTrue(127 == v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") short v) {
        Assertions.assertTrue(v == 32767, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") int v) {
        Assertions.assertEquals(2147483647, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") long v) {
        Assertions.assertEquals(9223372036854775807L, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") char v) {
        Assertions.assertEquals('a', v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
