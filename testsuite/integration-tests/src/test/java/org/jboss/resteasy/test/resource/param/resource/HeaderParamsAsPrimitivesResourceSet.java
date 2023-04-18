package org.jboss.resteasy.test.resource.param.resource;

import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

@Path("/set")
public class HeaderParamsAsPrimitivesResourceSet implements HeaderParamsAsPrimitivesSetProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("header") Set<String> v) {
        Assert.assertEquals(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, 2, v.size());
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, v.contains("one"));
        Assert.assertTrue(HeaderParamsAsPrimitivesTest.ERROR_MESSAGE, v.contains("two"));
        return "content";
    }
}
