package org.jboss.resteasy.test.asyncio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

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