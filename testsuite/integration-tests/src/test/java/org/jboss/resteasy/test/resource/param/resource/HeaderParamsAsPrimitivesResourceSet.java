package org.jboss.resteasy.test.resource.param.resource;

import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/set")
public class HeaderParamsAsPrimitivesResourceSet implements HeaderParamsAsPrimitivesSetProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("header") Set<String> v) {
        Assertions.assertEquals(2, v.size(), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(v.contains("one"), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(v.contains("two"), HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
