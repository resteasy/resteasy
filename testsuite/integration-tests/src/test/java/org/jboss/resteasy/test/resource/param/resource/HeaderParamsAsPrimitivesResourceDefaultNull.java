package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/default/null")
public class HeaderParamsAsPrimitivesResourceDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") boolean v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, false, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") byte v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") short v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") int v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") long v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") float v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0.0f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") double v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0.0d, v, 0.0);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") char v) {
    	Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, Character.MIN_VALUE, v);
        return "content";
    }
}
