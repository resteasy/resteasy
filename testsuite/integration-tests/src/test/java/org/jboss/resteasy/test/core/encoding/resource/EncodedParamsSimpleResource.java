package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.core.encoding.EncodedParamsTest;
import org.junit.jupiter.api.Assertions;

@Path("/encodedMethod")
public class EncodedParamsSimpleResource {
    @GET
    @Encoded
    public String get(@QueryParam("stuff") String stuff) {
        Assertions.assertEquals("hello%20world", stuff, EncodedParamsTest.ERROR_MESSAGE);
        return "HELLO";
    }

    @GET
    @Encoded
    @Path("/{param}")
    public String goodbye(@PathParam("param") String stuff) {
        Assertions.assertEquals("hello%20world", stuff, EncodedParamsTest.ERROR_MESSAGE);
        return "GOODBYE";
    }
}
