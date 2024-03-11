package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/array")
public class HeaderParamsAsPrimitivesResourceArray implements HeaderParamsAsPrimitivesArrayProxy {
    @GET
    @Produces("application/boolean")
    public String doGetBoolean(@HeaderParam("boolean") boolean[] v) {
        Assertions.assertEquals(true, v[0], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v[1], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertEquals(true, v[2], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGetShort(@HeaderParam("short") short[] v) {
        Assertions.assertTrue(32767 == v[0], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(32767 == v[0], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        Assertions.assertTrue(32767 == v[0], HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
