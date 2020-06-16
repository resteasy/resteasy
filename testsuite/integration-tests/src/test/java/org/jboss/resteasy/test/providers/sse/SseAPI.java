package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.SseEventSink;

public interface SseAPI {
    @Path("/events")
    @GET
    @Produces({ "text/event-stream" })
    void events(@Context SseEventSink sseEvents) throws Exception;

    @Path("send")
    @POST
    void send(String msg);
}