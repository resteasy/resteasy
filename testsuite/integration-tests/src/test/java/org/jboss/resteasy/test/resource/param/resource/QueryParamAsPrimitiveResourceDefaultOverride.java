package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.resource.param.QueryParamAsPrimitiveTest;
import org.junit.Assert;

@Path("/default/override")
public class QueryParamAsPrimitiveResourceDefaultOverride {
    @GET
    @Produces("application/boolean")
    public String doGet(@QueryParam("boolean") @DefaultValue("false") boolean v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, true, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@QueryParam("byte") @DefaultValue("1") byte v) {
        Assert.assertTrue((byte) 127 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@QueryParam("short") @DefaultValue("1") short v) {
        Assert.assertTrue((short) 32767 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@QueryParam("int") @DefaultValue("1") int v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 2147483647, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@QueryParam("long") @DefaultValue("1") long v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 9223372036854775807L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@QueryParam("float") @DefaultValue("0.0") float v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@QueryParam("double") @DefaultValue("0.0") double v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 3.14159265358979d, v, 0.0);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@QueryParam("char") @DefaultValue("b") char v) {
        Assert.assertEquals(QueryParamAsPrimitiveTest.ERROR_MESSAGE, 'a', v);
        return "content";
    }
}
