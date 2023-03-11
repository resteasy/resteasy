package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/array/default/null")
public class QueryParamAsPrimitiveResourceArrayDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@QueryParam("boolean") boolean[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@QueryParam("byte") byte[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@QueryParam("short") short[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@QueryParam("int") int[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@QueryParam("long") long[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@QueryParam("float") float[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@QueryParam("double") double[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGetCharacter(@QueryParam("char") char[] v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 0, v.length);
        return "content";
    }
}
