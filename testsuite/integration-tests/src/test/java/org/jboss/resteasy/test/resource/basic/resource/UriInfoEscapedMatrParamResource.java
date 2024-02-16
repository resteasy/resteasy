package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;

import org.junit.jupiter.api.Assertions;

@Path("/queryEscapedMatrParam")
public class UriInfoEscapedMatrParamResource {
    private static final String ERROR_MSG = "Wrong parameter";

    @GET
    public String doGet(@MatrixParam("a") String a, @MatrixParam("b") String b, @MatrixParam("c") String c,
            @MatrixParam("d") String d) {
        Assertions.assertEquals("a;b", a, ERROR_MSG);
        Assertions.assertEquals("x/y", b, ERROR_MSG);
        Assertions.assertEquals("m\\n", c, ERROR_MSG);
        Assertions.assertEquals("k=l", d, ERROR_MSG);
        return "content";
    }
}
