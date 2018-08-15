package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/list/default/null")
public class HeaderParamsAsPrimitivesResourceListDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") List<Boolean> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGetByte(@HeaderParam("byte") List<Byte> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") List<Short> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGetInteger(@HeaderParam("int") List<Integer> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGetLong(@HeaderParam("long") List<Long> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGetFloat(@HeaderParam("float") List<Float> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGetDouble(@HeaderParam("double") List<Double> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }
    
    @GET
    @Produces("application/char")
    public String doGetCharacter(@HeaderParam("char") List<Character> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 0, v.size());
        return "content";
    }
}
