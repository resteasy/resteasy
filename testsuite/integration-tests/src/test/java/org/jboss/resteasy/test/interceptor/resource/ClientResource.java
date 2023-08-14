package org.jboss.resteasy.test.interceptor.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@Path("/")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class ClientResource {
    @Inject
    private UriInfo uriInfo;

    @GET
    @Path("testIt")
    public Response get() {
        // we need to create new client to verify that @Provider works
        Client client = ClientBuilder.newClient();
        try {
            WebTarget base = client.target(uriInfo.getBaseUriBuilder().path("clientInvoke").build());
            Response response = base.request().get();

            // return the client invocation response to make the verification in test class
            return response;
        } finally {
            client.close();
        }
    }

    @GET
    @Path("clientInvoke")
    public Response clientInvoke() {
        return Response.ok().build();
    }
}
