package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/array/default")
public class QueryParamAsPrimitiveResourceArrayDefault {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") @DefaultValue("true") boolean[] v) {
        Assertions.assertEquals(true, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") @DefaultValue("127") byte[] v) {
        Assertions.assertTrue((byte) 127 == v[0]);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") @DefaultValue("32767") short[] v) {
        Assertions.assertTrue((short) 32767 == v[0]);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") @DefaultValue("2147483647") int[] v) {
        Assertions.assertEquals(2147483647, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") @DefaultValue("9223372036854775807") long[] v) {
        Assertions.assertEquals(9223372036854775807L, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") @DefaultValue("3.14159265") float[] v) {
        Assertions.assertEquals(3.14159265f, v[0], 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") @DefaultValue("3.14159265358979") double[] v) {
        Assertions.assertEquals(3.14159265358979d, v[0], 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") @DefaultValue("a") char[] v) {
        Assertions.assertEquals('a', v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
