package org.jboss.resteasy.test.providers.jackson2.resource;

import java.time.ZonedDateTime;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/status")
public class PortalStatusResource {

    @Inject
    private UriInfo uriInfo;

    @Path("/jsr310")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response jsr310Response() {
        JSR310Response response = new JSR310Response();
        response.setServerTime(ZonedDateTime.now());
        return Response.ok(response).build();
    }

    @Path("/time")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response trigger() {
        try (Client client = ClientBuilder.newBuilder().build()) {
            JSR310Response response = client.target(uriInfo.getBaseUriBuilder().path("/status/jsr310"))
                    .request()
                    .get()
                    .readEntity(JSR310Response.class);
            return Response.ok(response).build();
        }
    }

    @Path("/time/register/1")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response triggerRegistered1() {
        try (Client client = ClientBuilder.newBuilder().register(JacksonDisableTimeStampProducer.class).build()) {
            JSR310Response response = client.target(uriInfo.getBaseUriBuilder().path("/status/jsr310"))
                    .request()
                    .get()
                    .readEntity(JSR310Response.class);
            return Response.ok(response).build();
        }
    }

    public static class JSR310Response {

        private ZonedDateTime serverTime;

        public ZonedDateTime getServerTime() {
            return serverTime;
        }

        public void setServerTime(ZonedDateTime serverTime) {
            this.serverTime = serverTime;
        }
    }
}
