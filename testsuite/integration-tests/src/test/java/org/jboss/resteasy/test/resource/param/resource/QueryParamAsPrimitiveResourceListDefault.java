package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/list/default")
public class QueryParamAsPrimitiveResourceListDefault {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") @DefaultValue("true") List<Boolean> v) {
        Assertions.assertEquals(true, v.get(0).booleanValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") @DefaultValue("127") List<Byte> v) {
        Assertions.assertTrue((byte) 127 == v.get(0).byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") @DefaultValue("32767") List<Short> v) {
        Assertions.assertTrue((short) 32767 == v.get(0).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") @DefaultValue("2147483647") List<Integer> v) {
        Assertions.assertEquals(2147483647, v.get(0).intValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") @DefaultValue("9223372036854775807") List<Long> v) {
        Assertions.assertEquals(9223372036854775807L, v.get(0).longValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") @DefaultValue("3.14159265") List<Float> v) {
        Assertions.assertEquals(3.14159265f, v.get(0).floatValue(), 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") @DefaultValue("3.14159265358979") List<Double> v) {
        Assertions.assertEquals(3.14159265358979d, v.get(0).doubleValue(), 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") @DefaultValue("a") List<Character> v) {
        Assertions.assertEquals('a', v.get(0).charValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
