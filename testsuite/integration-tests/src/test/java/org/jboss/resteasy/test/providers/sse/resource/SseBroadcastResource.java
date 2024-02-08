package org.jboss.resteasy.test.providers.sse.resource;

import java.io.IOException;

import jakarta.ejb.Singleton;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.logging.Logger;

@Singleton
@Path("/broadcast")
public class SseBroadcastResource {

    private final Object sseBroadcasterLock = new Object();
    private volatile SseBroadcaster sseBroadcaster;
    private volatile boolean onErrorCalled = false;
    private volatile boolean onCloseCalled = false;

    private static final Logger logger = Logger.getLogger(SseBroadcastResource.class);

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

    @GET
    @Path("/closeSink")
    public void closeSink() throws IOException {
        if (this.sseBroadcaster == null) {
            throw new IllegalStateException("No Sse broadcaster created.");
        }
        this.eventSink.close();
        logger.info("Sink closed: " + eventSink.isClosed());
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
    public void close() throws IOException {
        synchronized (this.sseBroadcasterLock) {
            if (sseBroadcaster != null) {
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
