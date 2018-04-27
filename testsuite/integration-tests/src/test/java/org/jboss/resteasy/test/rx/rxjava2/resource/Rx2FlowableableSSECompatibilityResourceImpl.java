package org.jboss.resteasy.test.rx.rxjava2.resource;


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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

@Path("")
public class Rx2FlowableableSSECompatibilityResourceImpl {

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
   
   @GET
   @Path("flowable/thing")
   @Produces("text/event-stream")
   @SseElementType("application/json")
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