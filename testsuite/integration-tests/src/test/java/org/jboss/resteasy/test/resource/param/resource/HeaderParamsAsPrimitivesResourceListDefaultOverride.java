package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/list/default/override")
public class HeaderParamsAsPrimitivesResourceListDefaultOverride
        implements
        HeaderParamsAsPrimitivesListDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") @DefaultValue("false") List<Boolean> v) {
        Assertions.assertEquals(true, v.get(0).booleanValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@HeaderParam("byte") @DefaultValue("0") List<Byte> v) {
        Assertions.assertTrue(127 == v.get(0).byteValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") @DefaultValue("0") List<Short> v) {
        Assertions.assertTrue(32767 == v.get(0).shortValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@HeaderParam("int") @DefaultValue("0") List<Integer> v) {
        Assertions.assertEquals(2147483647, v.get(0).intValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@HeaderParam("long") @DefaultValue("0") List<Long> v) {
        Assertions.assertEquals(9223372036854775807L, v.get(0).longValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@HeaderParam("float") @DefaultValue("0.0") List<Float> v) {
        Assertions.assertEquals(3.14159265f, v.get(0).floatValue(), 0.0f, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@HeaderParam("double") @DefaultValue("0.0") List<Double> v) {
        Assertions.assertEquals(3.14159265358979d, v.get(0).doubleValue(), 0.0, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@HeaderParam("char") @DefaultValue("b") List<Character> v) {
        Assertions.assertEquals('a', v.get(0).charValue(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
