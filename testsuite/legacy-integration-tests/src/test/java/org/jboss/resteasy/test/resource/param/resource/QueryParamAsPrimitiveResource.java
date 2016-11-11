package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class QueryParamAsPrimitiveResource implements QueryParamAsPrimitiveResourceQueryPrimitivesInterface {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") boolean v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") byte v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (byte) 127 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") short v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, (short) 32767 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") int v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") long v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") float v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") double v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v, 0.0);
        return "content";
    }
}
