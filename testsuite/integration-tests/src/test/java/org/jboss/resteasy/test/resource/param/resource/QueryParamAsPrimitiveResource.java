package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class QueryParamAsPrimitiveResource implements QueryParamAsPrimitiveResourceQueryPrimitivesInterface {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") boolean v) {
        Assertions.assertEquals(true, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") byte v) {
        Assertions.assertTrue((byte) 127 == v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") short v) {
        Assertions.assertTrue((short) 32767 == v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") int v) {
        Assertions.assertEquals(2147483647, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") long v) {
        Assertions.assertEquals(9223372036854775807L, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") float v) {
        Assertions.assertEquals(3.14159265f, v, 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") double v) {
        Assertions.assertEquals(3.14159265358979d, v, 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") char v) {
        Assertions.assertEquals('a', v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
