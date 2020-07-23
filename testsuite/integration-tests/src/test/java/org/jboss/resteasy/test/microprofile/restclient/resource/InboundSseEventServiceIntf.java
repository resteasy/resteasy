package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;

@RegisterRestClient(baseUri ="http://localhost:8080/InboundSseEvenTest")
@Path("/theService")
@Singleton
public interface InboundSseEventServiceIntf {

    @GET
    @Path("events")
    @Produces({MediaType.SERVER_SENT_EVENTS})
    Publisher<InboundSseEvent> getEvents();
}
