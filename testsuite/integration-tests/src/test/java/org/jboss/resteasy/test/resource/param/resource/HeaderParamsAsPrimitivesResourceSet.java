package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Set;

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
