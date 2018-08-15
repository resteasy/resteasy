package org.jboss.resteasy.test.rx.rxjava2.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.SseElementType;
import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.Flowable;

@Path("")
public interface Rx2FlowableableSSECompatibilityResource {

   @GET
   @Path("eventStream/thing")
   @Produces("text/event-stream")
   @SseElementType("application/json")
   void eventStreamThing(@Context SseEventSink eventSink, @Context Sse sse);
   
   @GET
   @Path("flowable/thing")
   @Produces("text/event-stream")
   @SseElementType("application/json")
   Flowable<Thing> flowableSSE();
}