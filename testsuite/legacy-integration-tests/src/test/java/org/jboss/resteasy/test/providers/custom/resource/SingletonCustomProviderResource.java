package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class SingletonCustomProviderResource {

    @POST
    @Consumes("application/octet-stream")
    public void testConsume(SingletonCustomProviderObject foo) {
    }


    @GET
    @Produces("application/octet-stream")
    public SingletonCustomProviderObject testProduce() {
        return new SingletonCustomProviderObject();
    }

}
