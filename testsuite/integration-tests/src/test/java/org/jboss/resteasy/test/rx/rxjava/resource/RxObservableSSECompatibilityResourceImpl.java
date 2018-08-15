package org.jboss.resteasy.test.rx.rxjava.resource;


import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.SseElementType;
import org.jboss.resteasy.test.rx.resource.Thing;

import rx.Observable;
import rx.Subscriber;

@Path("")
public class RxObservableSSECompatibilityResourceImpl {

   @GET
   @Path("eventStream/thing")
   @Produces("text/event-stream")
   @SseElementType("application/json")
   public void eventStreamThing(@Context SseEventSink eventSink,
      @Context Sse sse) {
      new ScheduledThreadPoolExecutor(5).execute(() -> {
         try (SseEventSink sink = eventSink) {
            OutboundSseEvent.Builder  builder = sse.newEventBuilder();
            eventSink.send(builder.data(new Thing("e1")).build());
            eventSink.send(builder.data(new Thing("e2")).build());
            eventSink.send(builder.data(new Thing("e3")).build());
         }
      });
   }
   
   @SuppressWarnings("deprecation")
   @GET
   @Path("observable/thing")
   @Produces("text/event-stream")
   @SseElementType("application/json")
   public Observable<Thing> observableSSE() {
      return Observable.create(
         new Observable.OnSubscribe<Thing>() {
            public void call(Subscriber<? super Thing> emitter) {
               emitter.onNext(new Thing("e1"));
               emitter.onNext(new Thing("e2"));
               emitter.onNext(new Thing("e3"));
               emitter.onCompleted();
            }
         });
   }
}