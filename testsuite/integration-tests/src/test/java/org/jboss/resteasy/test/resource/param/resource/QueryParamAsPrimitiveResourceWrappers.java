package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/wrappers")
public class QueryParamAsPrimitiveResourceWrappers implements QueryParamAsPrimitiveResourceResourceWrappersInterface {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") Boolean v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v.booleanValue());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") Byte v) {
        Assert.assertTrue((byte) 127 == v.byteValue());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") Short v) {
        Assert.assertTrue((short) 32767 == v.shortValue());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") Integer v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v.intValue());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") Long v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v.longValue());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") Float v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v.floatValue(), 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") Double v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v.doubleValue(), 0.0);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") Character v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v.charValue());
        return "content";
    }
}
