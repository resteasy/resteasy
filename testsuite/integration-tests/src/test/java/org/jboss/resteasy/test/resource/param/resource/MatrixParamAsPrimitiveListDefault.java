package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/list/default")
public class MatrixParamAsPrimitiveListDefault {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("true") List<Boolean> v) {
        Assertions.assertEquals(true, v.get(0).booleanValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@MatrixParam("byte") @DefaultValue("127") List<Byte> v) {
        Assertions.assertTrue((byte) 127 == v.get(0).byteValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@MatrixParam("short") @DefaultValue("32767") List<Short> v) {
        Assertions.assertTrue((short) 32767 == v.get(0).shortValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@MatrixParam("int") @DefaultValue("2147483647") List<Integer> v) {
        Assertions.assertEquals(2147483647, v.get(0).intValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@MatrixParam("long") @DefaultValue("9223372036854775807") List<Long> v) {
        Assertions.assertEquals(9223372036854775807L, v.get(0).longValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@MatrixParam("float") @DefaultValue("3.14159265") List<Float> v) {
        Assertions.assertEquals(3.14159265f, v.get(0).floatValue(), 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@MatrixParam("double") @DefaultValue("3.14159265358979") List<Double> v) {
        Assertions.assertEquals(3.14159265358979d, v.get(0).doubleValue(), 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@MatrixParam("char") @DefaultValue("a") List<Character> v) {
        Assertions.assertEquals('a', v.get(0).charValue(), MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
