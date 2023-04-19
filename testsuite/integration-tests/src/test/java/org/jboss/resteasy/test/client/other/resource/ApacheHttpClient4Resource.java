package org.jboss.resteasy.test.client.other.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public interface ApacheHttpClient4Resource {
    @GET
    @Produces("text/plain")
    String get();

    @GET
    @Path("error")
    @Produces("text/plain")
    String error();

    @POST
    @Path("data")
    @Produces("text/plain")
    @Consumes("text/plain")
    String getData(String data);
}
