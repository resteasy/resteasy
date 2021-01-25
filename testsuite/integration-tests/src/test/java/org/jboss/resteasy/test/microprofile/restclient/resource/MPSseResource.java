package org.jboss.resteasy.test.microprofile.restclient.resource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.test.providers.sse.ExecutorServletContextListener;

@Path("/")
public class MPSseResource {

    @Context
    private Sse sse;

    @Context
    private ServletContext servletContext;

    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void eventStream(@Context SseEventSink sink) throws IOException {
        if (sink == null) {
            throw new IllegalStateException("No client connected.");
        }
        ExecutorService service = (ExecutorService) servletContext.getAttribute(ExecutorServletContextListener.TEST_EXECUTOR);
        service.execute(() -> {
            for (int i = 0; i < 12; i++) {
                sink.send(sse.newEvent("msg" + i));
            }
        });
    }
}
