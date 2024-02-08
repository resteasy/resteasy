package org.jboss.resteasy.test.asyncio;

import java.io.IOException;
import java.io.UncheckedIOException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("close")
public class SSEResource {

    private static volatile boolean exception = false;

    private static volatile boolean isClosed = false;

    @GET
    @Path("reset")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void reset(@Context SseEventSink sink, @Context Sse sse) {
        exception = false;
        isClosed = false;
        sink.send(sse.newEvent("RESET"));
    }

    @GET
    @Path("send")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void send(@Context SseEventSink sink, @Context Sse sse) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    SseEventSink s = sink;
                    s.send(sse.newEvent("HELLO"));
                    s.close();
                    isClosed = s.isClosed();
                    if (!isClosed)
                        return;
                    s.close();
                    isClosed = s.isClosed();
                    if (!isClosed)
                        return;
                    s.close();
                    isClosed = s.isClosed();
                    if (!isClosed)
                        return;
                    try {
                        s.send(sse.newEvent("SOMETHING")).exceptionally(t -> {
                            if (t instanceof IllegalStateException)
                                exception = true;
                            return null;
                        });
                    } catch (IllegalStateException ise) {
                        exception = true;
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });
        t.start();
    }

    @GET
    @Path("check")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void check(@Context SseEventSink sink, @Context Sse sse) {
        if (!isClosed) {
            sink.send(sse.newEvent("Not closed"));
            return;
        }
        if (!exception) {
            sink.send(sse.newEvent("No IllegalStateException is thrown"));
            return;
        }
        sink.send(sse.newEvent("CHECK"));
    }

    @GET
    @Path("closed")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isClosed() {
        return isClosed;
    }
}