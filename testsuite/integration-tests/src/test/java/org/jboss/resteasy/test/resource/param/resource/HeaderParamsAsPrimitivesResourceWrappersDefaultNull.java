package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.HeaderParamsAsPrimitivesTest;
import org.junit.jupiter.api.Assertions;

@Path("/wrappers/default/null")
public class HeaderParamsAsPrimitivesResourceWrappersDefaultNull {
    @GET
    @Produces("application/boolean")
    public String doGet(@HeaderParam("boolean") Boolean v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/byte")
    public String doGet(@HeaderParam("byte") Byte v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/short")
    public String doGet(@HeaderParam("short") Short v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/int")
    public String doGet(@HeaderParam("int") Integer v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/long")
    public String doGet(@HeaderParam("long") Long v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/float")
    public String doGet(@HeaderParam("float") Float v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/double")
    public String doGet(@HeaderParam("double") Double v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Produces("application/char")
    public String doGet(@HeaderParam("char") Character v) {
        Assertions.assertEquals(null, v, HeaderParamsAsPrimitivesTest.ERROR_MESSAGE);
        return "content";
    }
}
