package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/array")
public class QueryParamAsPrimitiveResourceArray implements QueryParamAsPrimitiveResourceResourceArray {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") boolean[] v) {
        Assertions.assertEquals(true, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") byte[] v) {
        Assertions.assertTrue((byte) 127 == v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @POST
    @Produces("application/byte")
    public String doPostByte(@QueryParam("byte") byte[] v) {
        Assertions.assertTrue((byte) 127 == v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((byte) 127 == v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") short[] v) {
        Assertions.assertTrue((short) 32767 == v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((short) 32767 == v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertTrue((short) 32767 == v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") int[] v) {
        Assertions.assertEquals(2147483647, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(2147483647, v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") long[] v) {
        Assertions.assertEquals(9223372036854775807L, v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(9223372036854775807L, v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") float[] v) {
        Assertions.assertEquals(3.14159265f, v[0], 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v[1], 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265f, v[2], 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") double[] v) {
        Assertions.assertEquals(3.14159265358979d, v[0], 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v[1], 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals(3.14159265358979d, v[2], 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") char[] v) {
        Assertions.assertEquals('a', v[0], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v[1], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        Assertions.assertEquals('a', v[2], QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
