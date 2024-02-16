package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/list")
public class HeaderParamsAsPrimitivesResourceList implements HeaderParamsAsPrimitivesListProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") List<Boolean> v) {
        Assertions.assertEquals(true, v.get(0).booleanValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v.get(1).booleanValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v.get(2).booleanValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@HeaderParam("byte") List<Byte> v) {
        Assertions.assertTrue(127 == v.get(0).byteValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(127 == v.get(1).byteValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(127 == v.get(2).byteValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") List<Short> v) {
        Assertions.assertTrue(32767 == v.get(0).shortValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(32767 == v.get(1).shortValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(32767 == v.get(2).shortValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@HeaderParam("int") List<Integer> v) {
        Assertions.assertEquals(2147483647, v.get(0).intValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v.get(1).intValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v.get(2).intValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@HeaderParam("long") List<Long> v) {
        Assertions.assertEquals(9223372036854775807L, v.get(0).longValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v.get(1).longValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v.get(2).longValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@HeaderParam("float") List<Float> v) {
        Assertions.assertEquals(3.14159265f, v.get(0).floatValue(), 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v.get(1).floatValue(), 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v.get(2).floatValue(), 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@HeaderParam("double") List<Double> v) {
        Assertions.assertEquals(3.14159265358979d, v.get(0).doubleValue(), 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v.get(1).doubleValue(), 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v.get(2).doubleValue(), 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@HeaderParam("char") List<Character> v) {
        Assertions.assertEquals('a', v.get(0).charValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v.get(1).charValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v.get(2).charValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
