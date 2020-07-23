package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reactivestreams.Publisher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.inject.Singleton;

@RegisterProvider(WeatherEventProviderJSON.class)
@RegisterProvider(WeatherEventProvider.class)
@RegisterRestClient(baseUri ="http://localhost:8080/WeatherEventTest")
@Path("/theService")
@Singleton
public interface WeatherEventClient {
    @GET
    @Path("events")
    @Produces({MediaType.SERVER_SENT_EVENTS})
    Publisher<WeatherEvent> getEvents();

    @GET
    @Path("eventsJson")
    @Produces("text/event-stream;element-type=application/json")
    Publisher<WeatherEvent> getEventsJson();
}
