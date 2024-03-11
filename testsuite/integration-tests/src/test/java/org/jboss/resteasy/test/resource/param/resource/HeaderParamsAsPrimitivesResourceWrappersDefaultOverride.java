package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default/override")
public class HeaderParamsAsPrimitivesResourceWrappersDefaultOverride
        implements
        HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") @DefaultValue("false") Boolean v) {
        Assertions.assertEquals(true, v.booleanValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") @DefaultValue("1") Byte v) {
        Assertions.assertTrue(127 == v.byteValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") @DefaultValue("1") Short v) {
        Assertions.assertTrue(32767 == v.shortValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") @DefaultValue("1") Integer v) {
        Assertions.assertEquals(2147483647, v.intValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") @DefaultValue("1") Long v) {
        Assertions.assertEquals(9223372036854775807L, v.longValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") @DefaultValue("0.0") Float v) {
        Assertions.assertEquals(3.14159265f, v.floatValue(), 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") @DefaultValue("0.0") Double v) {
        Assertions.assertEquals(3.14159265358979d, v.doubleValue(), 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") @DefaultValue("b") Character v) {
        Assertions.assertEquals('a', v.charValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
