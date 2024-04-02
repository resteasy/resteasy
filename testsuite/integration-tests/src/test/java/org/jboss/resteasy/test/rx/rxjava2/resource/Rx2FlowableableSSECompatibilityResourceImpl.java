package org.jboss.resteasy.test.rx.rxjava2.resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

@Path("")
public class Rx2FlowableableSSECompatibilityResourceImpl {

    @GET
    @Path("eventStream/thing")
    @Produces("text/event-stream;element-type=application/json")
    public void eventStreamThing(@Context SseEventSink eventSink,
            @Context Sse sse) {
        new ScheduledThreadPoolExecutor(5).execute(() -> {
            try (SseEventSink sink = eventSink) {
                OutboundSseEvent.Builder builder = sse.newEventBuilder();
                eventSink.send(builder.data(new Thing("e1")).build());
                eventSink.send(builder.data(new Thing("e2")).build());
                eventSink.send(builder.data(new Thing("e3")).build());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @GET
    @Path("flowable/thing")
    @Produces("text/event-stream;element-type=application/json")
    public Flowable<Thing> flowableSSE() {
        return Flowable.create(
                new FlowableOnSubscribe<Thing>() {

                    @Override
                    public void subscribe(FlowableEmitter<Thing> emitter) throws Exception {
                        emitter.onNext(new Thing("e1"));
                        emitter.onNext(new Thing("e2"));
                        emitter.onNext(new Thing("e3"));
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }
}
