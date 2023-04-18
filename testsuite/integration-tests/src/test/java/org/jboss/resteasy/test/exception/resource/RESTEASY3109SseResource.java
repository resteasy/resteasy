package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/sse")
public class RESTEASY3109SseResource {

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public String getEventSink(@Context SseEventSink sseEventSink) {
        return "got to resource";
    }
}
