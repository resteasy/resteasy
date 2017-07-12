package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/default/override")
public class HeaderParamsAsPrimitivesResourceDefaultOverride implements HeaderParamsAsPrimitivesDefaultOverrideProxy {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") @DefaultValue("false") boolean v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, true, v);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") @DefaultValue("1") byte v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 127 == v);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") @DefaultValue("1") short v) {
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 32767 == v);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") @DefaultValue("1") int v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2147483647, v);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") @DefaultValue("1") long v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 9223372036854775807L, v);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") @DefaultValue("0.0") float v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265f, v, 0.0f);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") @DefaultValue("0.0") double v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 3.14159265358979d, v, 0.0);
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") @DefaultValue("b") char v) {
    	Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 'a', v);
        return "content";
    }
}
