package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

@Path("/test")
public class LazyInitUriInfoInjectionResource {
    private static Logger logger = Logger.getLogger(LazyInitUriInfoInjectionResource.class);

    private UriInfo info;

    @Inject
    public void setUriInfo(UriInfo i) {
        this.info = i;
        logger.info(i.getClass().getName());
    }

    @GET
    @Produces("text/plain")
    public String get() {
        String val = info.getQueryParameters().getFirst("h");
        if (val == null) {
            val = "";
        }
        return val;
    }

    @GET
    @Path("/template/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clientTemplate(@PathParam("clientId") final String clientId) {
        // Create the JSON object
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("clientId", clientId)
                .add("path", info.getPath())
                .add("matchedResourceTemplate", info.getMatchedResourceTemplate());
        return Response.ok(builder.build()).build();
    }

}
