package org.jboss.resteasy.test.client.resource;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
