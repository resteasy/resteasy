package org.jboss.resteasy.test.providers.sse.resource;

import org.jboss.logging.Logger;

import javax.ejb.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.io.IOException;

@Singleton
@Path("/broadcast")
public class SseBroadcastResource {

    private final Object sseBroadcasterLock = new Object();
    private volatile SseBroadcaster sseBroadcaster;
    private volatile boolean onErrorCalled = false;
    private volatile boolean onCloseCalled = false;

    private final static Logger logger = Logger.getLogger(SseBroadcastResource.class);

    private volatile SseEventSink eventSink;

    @GET
    @Path("/subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sink, @Context Sse sse) throws IOException {

        if (sink == null) {
            throw new IllegalStateException("No client connected.");
        }
        synchronized (this.sseBroadcasterLock) {
            //subscribe
            if (sseBroadcaster == null) {
                sseBroadcaster = sse.newBroadcaster();
                onCloseCalled = false;
                onErrorCalled = false;
            }
        }
        sseBroadcaster.register(sink);
        this.eventSink = sink;
        logger.info("Sink registered");
    }

    @POST
    @Path("/start")
    public void broadcast(String message, @Context Sse sse) throws IOException {
        if (this.sseBroadcaster == null) {
            throw new IllegalStateException("No Sse broadcaster created.");
        }
        this.sseBroadcaster.broadcast(sse.newEvent(message));
    }

    @POST
    @Path("/startAndClose")
    public void broadcastAndClose(String message, @Context Sse sse) throws IOException, InterruptedException {
        if (this.sseBroadcaster == null) {
            throw new IllegalStateException("No Sse broadcaster created.");
        }
        this.eventSink.close();
        logger.info("Sink closed: " + eventSink.isClosed());
        this.sseBroadcaster.broadcast(sse.newEvent(message));
    }

    @GET
    @Path("/listeners")
    public void registerListeners(@Context Sse sse) throws IOException {

        synchronized (this.sseBroadcasterLock) {
            if (sseBroadcaster == null) {
                sseBroadcaster = sse.newBroadcaster();
                onCloseCalled = false;
                onErrorCalled = false;
            }
            sseBroadcaster.onClose(sseEventSink -> {
                onCloseCalled = true;
                logger.info("onClose called");
            });
            sseBroadcaster.onError((sseEventSink, throwable) -> {
                onErrorCalled = true;
                logger.info("onError called");
            });
        }
    }

    @DELETE
    public void close() throws IOException
    {
        synchronized (this.sseBroadcasterLock)
        {
            if (sseBroadcaster != null)
            {
                sseBroadcaster.close();
                sseBroadcaster = null;
            }
        }
    }

    @GET
    @Path("/onCloseCalled")
    public boolean onCloseCalled() {
        synchronized (this.sseBroadcasterLock) {
            return onCloseCalled;
        }
    }

    @GET
    @Path("/onErrorCalled")
    public boolean onErrorCalled() {
        synchronized (this.sseBroadcasterLock) {
            return onErrorCalled;
        }
    }
}
