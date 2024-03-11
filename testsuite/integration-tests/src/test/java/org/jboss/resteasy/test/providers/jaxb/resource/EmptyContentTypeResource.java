package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;

@Path("/test")
public class EmptyContentTypeResource {
    @POST
    public Response postNada(@HeaderParam("Content-Type") String contentType) {
        Assertions.assertEquals(null, contentType);
        return Response.ok("NULL", "text/plain").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response post(EmptyContentTypeFoo foo) {
        return Response.ok(foo.getName(), "text/plain").build();
    }
}
