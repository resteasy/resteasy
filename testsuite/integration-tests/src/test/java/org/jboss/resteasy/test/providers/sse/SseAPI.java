package org.jboss.resteasy.test.providers.sse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.SseEventSink;

public interface SseAPI {
    @Path("/events")
    @GET
    @Produces({ "text/event-stream" })
    void events(@Context SseEventSink sseEvents) throws Exception;

    @Path("send")
    @POST
    void send(String msg);
}