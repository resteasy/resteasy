package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/default/override")
public class HeaderParamsAsPrimitivesResourceDefaultOverride implements HeaderParamsAsPrimitivesDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") @DefaultValue("false") boolean v) {
        Assertions.assertEquals(true, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") @DefaultValue("1") byte v) {
        Assertions.assertTrue(127 == v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") @DefaultValue("1") short v) {
        Assertions.assertTrue(32767 == v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") @DefaultValue("1") int v) {
        Assertions.assertEquals(2147483647, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") @DefaultValue("1") long v) {
        Assertions.assertEquals(9223372036854775807L, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") @DefaultValue("0.0") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") @DefaultValue("0.0") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") @DefaultValue("b") char v) {
        Assertions.assertEquals('a', v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
