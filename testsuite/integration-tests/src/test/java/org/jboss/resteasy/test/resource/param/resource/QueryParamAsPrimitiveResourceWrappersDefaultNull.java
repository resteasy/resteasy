package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default/null")
public class QueryParamAsPrimitiveResourceWrappersDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") Boolean v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") Byte v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") Short v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") Integer v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") Long v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") Float v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") Double v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") Character v) {
        Assertions.assertEquals(null, v, QueryParamAsPrimitiveTest.ERROR_MESSAGE);
        return "content";
    }
}
