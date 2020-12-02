package org.jboss.resteasy.test.microprofile.restclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reactivestreams.Publisher;

@RegisterRestClient(baseUri = "http://localhost:8080/SsePublisherClientTest")
public interface MPSseClient {

    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    Publisher<String> getStrings();
}
