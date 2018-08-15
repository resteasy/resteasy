package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/default/null")
public class QueryParamAsPrimitiveResourceDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") boolean v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, false, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") byte v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") short v) {
        Assert.assertTrue(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") int v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE,  0, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") long v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE,  0L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") float v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE,  0.0f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") double v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE,  0.0d, v, 0.0);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") char v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, Character.MIN_VALUE, v);
        return "content";
    }
}
