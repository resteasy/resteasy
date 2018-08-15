package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/wrappers/default/null")
public class QueryParamAsPrimitiveResourceWrappersDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") Boolean v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") Byte v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") Short v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") Integer v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") Long v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") Float v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") Double v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") Character v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, null, v);
    	return "content";
    }
}
