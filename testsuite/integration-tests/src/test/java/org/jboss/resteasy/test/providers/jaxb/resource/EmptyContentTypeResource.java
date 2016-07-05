package org.jboss.resteasy.test.providers.jaxb.resource;

import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public class EmptyContentTypeResource {
    @POST
    public Response postNada(@HeaderParam("Content-Type") String contentType) {
        Assert.assertEquals(null, contentType);
        return Response.ok("NULL", "text/plain").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response post(EmptyContentTypeFoo foo) {
        return Response.ok(foo.getName(), "text/plain").build();
    }
}
