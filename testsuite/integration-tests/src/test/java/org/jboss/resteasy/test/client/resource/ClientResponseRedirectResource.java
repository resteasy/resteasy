package org.jboss.resteasy.test.client.resource;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.test.client.ClientResponseRedirectTest;
import org.jboss.resteasy.utils.PortProviderUtil;

@Path("/redirect")
public class ClientResponseRedirectResource {
    @GET
    public Response get() {
        try {
            return Response
                    .seeOther(URI.create(
                            PortProviderUtil.generateURL("/redirect/data", ClientResponseRedirectTest.class.getSimpleName())))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("data")
    public String getData() {
        return "data";
    }
}
