package org.jboss.resteasy.test.client.resource;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
public class AbortMessageResourceFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.abortWith(Response.ok("aborted").header("Aborted", "true").type(MediaType.TEXT_PLAIN_TYPE).build());
    }

    @Path("/showproblem")
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public Response showProblem() {
        Client c = ClientBuilder.newClient().register(this);
        return c.target("http://doesnotmattersinceitisaborted").request().get();
    }
}
