package org.jboss.resteasy.test.microprofile.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reactivestreams.Publisher;

@RegisterRestClient(baseUri = "http://localhost:8080/SsePublisherClientTest")
public interface MPSseClient {

    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    Publisher<String> getStrings();
}
