package org.jboss.resteasy.test.rx.rxjava2.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.Flowable;

@Path("")
public interface Rx2FlowableableSSECompatibilityResource {

   @GET
   @Path("eventStream/thing")
   @Produces("text/event-stream;element-type=application/json")
   void eventStreamThing(@Context SseEventSink eventSink, @Context Sse sse);

   @GET
   @Path("flowable/thing")
   @Produces("text/event-stream;element-type=application/json")
   Flowable<Thing> flowableSSE();
}
