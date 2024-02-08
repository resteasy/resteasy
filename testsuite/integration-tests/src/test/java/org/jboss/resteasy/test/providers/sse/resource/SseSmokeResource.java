package org.jboss.resteasy.test.providers.sse.resource;

import java.io.IOException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/sse")
public class SseSmokeResource {

    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sentEvents(@Context SseEventSink sseEventSink, @Context Sse sse) throws IOException {

        try (SseEventSink sink = sseEventSink) {
            sseEventSink.send(sse.newEventBuilder()
                    .name("customObj")
                    .data(new SseSmokeUser("Zeytin", "zeytin@resteasy.org")).build());
        }
    }

    @GET
    @Path("/eventssimple")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sentSimpleEvents(@Context SseEventSink sseEventSink, @Context Sse sse) throws IOException {

        try (SseEventSink sink = sseEventSink) {
            sseEventSink.send(sse.newEvent("data"));
        }
    }

    @GET
    @Path("/eventsjson")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sentJsonEvents(@Context SseEventSink sseEventSink, @Context Sse sse) throws IOException {
        OutboundSseEvent event = sse.newEventBuilder().name("json")
                .data("{\"email\":\"zeytin@resteasy.org\",\"username\":\"Zeytin\",\"nickname\":\"Zeytin\"}")
                .mediaType(MediaType.APPLICATION_JSON_TYPE).build();
        try (SseEventSink sink = sseEventSink) {
            sseEventSink.send(event);
        }
    }
}
