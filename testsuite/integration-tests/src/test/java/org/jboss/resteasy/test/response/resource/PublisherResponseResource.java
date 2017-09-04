package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.Stream;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@Path("")
public class PublisherResponseResource {
   
   @GET
   @Path("text")
   @Produces("application/json")
   public Publisher<String> text() {
	   return Flowable.fromArray("one", "two");
   }

   @Stream
   @GET
   @Path("chunked")
   @Produces("application/json")
   public Publisher<String> chunked() {
	   return Flowable.fromArray("one", "two");
   }

   @GET
   @Path("sse")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public Publisher<String> sse() {
	   return Flowable.fromArray("one", "two");
   }
}
