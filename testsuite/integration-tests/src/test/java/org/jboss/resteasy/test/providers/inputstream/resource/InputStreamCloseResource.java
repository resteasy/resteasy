package org.jboss.resteasy.test.providers.inputstream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/")
public class InputStreamCloseResource {
    private static InputStreamCloseInputStream inputStream;

    @GET
    @Produces("text/plain")
    @Path("create")
    public InputStream create() {
        inputStream = new InputStreamCloseInputStream("hello".getBytes());
        return inputStream;
    }

    @GET
    @Path("test")
    public Response test() {
        return (inputStream.isClosed() ? Response.ok().build() : Response.serverError().build());
    }
}
