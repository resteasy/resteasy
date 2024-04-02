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

import org.jboss.resteasy.annotations.SseElementType;
import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

@Path("")
public class Rx2ObservableSSECompatibilityResourceImpl {

    @GET
    @Path("eventStream/thing")
    @Produces("text/event-stream")
    @SseElementType("application/json")
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
    @Path("observable/thing")
    @Produces("text/event-stream")
    @SseElementType("application/json")
    public Observable<Thing> observableSSE() {
        return Observable.create(
                new ObservableOnSubscribe<Thing>() {

                    @Override
                    public void subscribe(ObservableEmitter<Thing> emitter) throws Exception {
                        emitter.onNext(new Thing("e1"));
                        emitter.onNext(new Thing("e2"));
                        emitter.onNext(new Thing("e3"));
                        emitter.onComplete();
                    }
                });
    }
}
