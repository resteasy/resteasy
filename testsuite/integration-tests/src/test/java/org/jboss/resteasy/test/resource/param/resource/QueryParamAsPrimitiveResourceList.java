package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/list")
public class QueryParamAsPrimitiveResourceList implements QueryParamAsPrimitiveResourceResourceListInterface {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") List<Boolean> v) {
        Assertions.assertEquals(true, v.get(0).booleanValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v.get(1).booleanValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v.get(2).booleanValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") List<Byte> v) {
        Assertions.assertTrue((byte) 127 == v.get(0).byteValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v.get(1).byteValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v.get(2).byteValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") List<Short> v) {
        Assertions.assertTrue((short) 32767 == v.get(0).shortValue());
        Assertions.assertTrue((short) 32767 == v.get(1).shortValue());
        Assertions.assertTrue((short) 32767 == v.get(2).shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") List<Integer> v) {
        Assertions.assertEquals(2147483647, v.get(0).intValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v.get(1).intValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v.get(2).intValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") List<Long> v) {
        Assertions.assertEquals(9223372036854775807L, v.get(0).longValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v.get(1).longValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v.get(2).longValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") List<Float> v) {
        Assertions.assertEquals(3.14159265f, v.get(0).floatValue(), 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v.get(1).floatValue(), 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v.get(2).floatValue(), 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") List<Double> v) {
        Assertions.assertEquals(3.14159265358979d, v.get(0).doubleValue(), 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v.get(1).doubleValue(), 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v.get(2).doubleValue(), 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") List<Character> v) {
        Assertions.assertEquals('a', v.get(0).charValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v.get(1).charValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v.get(2).charValue(), QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
