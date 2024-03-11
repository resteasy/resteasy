package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.MatrixParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/array/default/override")
public class MatrixParamAsPrimitiveArrayDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@MatrixParam("boolean") @DefaultValue("false") boolean[] v) {
        Assertions.assertEquals(true, v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@MatrixParam("byte") @DefaultValue("0") byte[] v) {
        Assertions.assertTrue((byte) 127 == v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@MatrixParam("short") @DefaultValue("0") short[] v) {
        Assertions.assertTrue((short) 32767 == v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@MatrixParam("int") @DefaultValue("0") int[] v) {
        Assertions.assertEquals(2147483647, v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@MatrixParam("long") @DefaultValue("0") long[] v) {
        Assertions.assertEquals(9223372036854775807L, v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@MatrixParam("float") @DefaultValue("0.0") float[] v) {
        Assertions.assertEquals(3.14159265f, v[0], 0.0f, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@MatrixParam("double") @DefaultValue("0.0") double[] v) {
        Assertions.assertEquals(3.14159265358979d, v[0], 0.0, MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@MatrixParam("char") @DefaultValue("b") char[] v) {
        Assertions.assertEquals('a', v[0], MatrixParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
