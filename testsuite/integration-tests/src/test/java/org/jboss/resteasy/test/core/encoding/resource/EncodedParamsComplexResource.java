package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.test.core.encoding.EncodedParamsTest;
import org.junit.jupiter.api.Assertions;

@Path("/encodedParam")
public class EncodedParamsComplexResource {
    @GET
    public String get(@QueryParam("hello world") int num, @QueryParam("stuff") @Encoded String stuff,
            @QueryParam("stuff") String unStuff) {
        Assertions.assertEquals(5, num, EncodedParamsTest.ERROR_MESSAGE);
        Assertions.assertEquals("hello%20world", stuff, EncodedParamsTest.ERROR_MESSAGE);
        Assertions.assertEquals("hello world", unStuff, EncodedParamsTest.ERROR_MESSAGE);
        return "HELLO";
    }

    @GET
    @Path("/{param}")
    public String goodbye(@PathParam("param") @Encoded String stuff) {
        Assertions.assertEquals("hello%20world", stuff, EncodedParamsTest.ERROR_MESSAGE);
        return "GOODBYE";
    }
}
