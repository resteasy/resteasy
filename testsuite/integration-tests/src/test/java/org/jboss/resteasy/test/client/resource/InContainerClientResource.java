package org.jboss.resteasy.test.client.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.UriInfo;

@Path("/test-client")
public class InContainerClientResource {

    @Inject
    private UriInfo uriInfo;

    @POST
    @Consumes("text/plain")
    public String post(String str) throws Exception {
        Client client = ClientBuilder.newClient();
        String result = null;
        try {
            result = client.target(uriInfo.getBaseUri() + "test").request().post(Entity.text(str)).readEntity(String.class);
        } finally {
            client.close();
        }
        return "client-post " + result;
    }
}
