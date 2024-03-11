package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/array/default/null")
public class QueryParamAsPrimitiveResourceArrayDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") boolean[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") byte[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") short[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") int[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") long[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") float[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") double[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") char[] v) {
        Assertions.assertEquals(0, v.length, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
