package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/default/null")
public class QueryParamAsPrimitiveResourceDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") boolean v) {
        Assertions.assertEquals(false, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") byte v) {
        Assertions.assertTrue(0 == v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") short v) {
        Assertions.assertTrue(0 == v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") int v) {
        Assertions.assertEquals(0, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") long v) {
        Assertions.assertEquals(0L, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") float v) {
        Assertions.assertEquals(0.0f, v, 0.0f, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") double v) {
        Assertions.assertEquals(0.0d, v, 0.0, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") char v) {
        Assertions.assertEquals(Character.MIN_VALUE, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
