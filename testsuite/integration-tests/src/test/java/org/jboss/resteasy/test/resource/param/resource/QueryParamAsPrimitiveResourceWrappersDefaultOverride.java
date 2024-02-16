package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default/override")
public class QueryParamAsPrimitiveResourceWrappersDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") @DefaultValue("false") Boolean v) {
        Assertions.assertEquals(true, v.booleanValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") @DefaultValue("1") Byte v) {
        Assertions.assertTrue((byte) 127 == v.byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") @DefaultValue("1") Short v) {
        Assertions.assertTrue((short) 32767 == v.shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") @DefaultValue("1") Integer v) {
        Assertions.assertEquals(2147483647, v.intValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") @DefaultValue("1") Long v) {
        Assertions.assertEquals(9223372036854775807L, v.longValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") @DefaultValue("0.0") Float v) {
        Assertions.assertEquals(3.14159265f, v.floatValue(), 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") @DefaultValue("0.0") Double v) {
        Assertions.assertEquals(3.14159265358979d, v.doubleValue(), 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") @DefaultValue("b") Character v) {
        Assertions.assertEquals('a', v.charValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
