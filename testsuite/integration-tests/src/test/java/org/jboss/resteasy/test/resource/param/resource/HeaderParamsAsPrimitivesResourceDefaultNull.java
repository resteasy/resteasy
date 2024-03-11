package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/default/null")
public class HeaderParamsAsPrimitivesResourceDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") boolean v) {
        Assertions.assertEquals(false, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") byte v) {
        Assertions.assertTrue(0 == v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") short v) {
        Assertions.assertTrue(0 == v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") int v) {
        Assertions.assertEquals(0, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") long v) {
        Assertions.assertEquals(0L, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") float v) {
        Assertions.assertEquals(0.0f, v, 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") double v) {
        Assertions.assertEquals(0.0d, v, 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") char v) {
        Assertions.assertEquals(Character.MIN_VALUE, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
