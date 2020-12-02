package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

@Path("")
public class PublisherResponseNoStreamResource {

   private static boolean terminated = false;
   private static final Logger LOG = Logger.getLogger(PublisherResponseNoStreamResource.class);

   @GET
   @Path("text")
   @Produces("application/json")
   public Publisher<String> text(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback("text"));
      return Flowable.create(source ->{
         for(int i=0;i<30;i++) {
            source.onNext(i+"-"+ResteasyContext.getContextDataLevelCount());
         }
         source.onComplete();
      }, BackpressureStrategy.BUFFER);
   }

   @GET
   @Path("text-infinite")
   @Produces("application/json")
   public Publisher<String> textInfinite() {
      terminated = false;
      LOG.error("Starting ");
      return Flowable.interval(1, TimeUnit.SECONDS).map(v -> {
         return "one";
      }).doFinally(() -> {
         terminated = true;
      });
   }

   @GET
   @Path("callback-called-no-error/{p}")
   public String callbackCalledNoError(@PathParam String p) {
      AsyncResponseCallback.assertCalled(p, false);
      return "OK";
   }

   @GET
   @Path("text-error-immediate")
   @Produces("application/json")
   public Publisher<String> textErrorImmediate(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback("text-error-immediate"));
      throw new AsyncResponseException();
   }

   @GET
   @Path("text-error-deferred")
   @Produces("application/json")
   public Publisher<String> textErrorDeferred(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback("text-error-deferred"));
      return Flowable.error(new AsyncResponseException());
   }

   @GET
   @Path("callback-called-with-error/{p}")
   public String callbackCalledWithError(@PathParam String p) {
      AsyncResponseCallback.assertCalled(p, true);
      return "OK";
   }

   @GET
   @Path("sse")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public Publisher<String> sse() {
      return Flowable.fromArray("one", "two");
   }

   @GET
   @Path("sse-infinite")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public Publisher<String> sseInfinite() {
      terminated = false;
      return Flowable.interval(1, TimeUnit.SECONDS).map(v -> {
         return "one";
      }).doFinally(() -> {
         terminated = true;
      });
   }

   @GET
   @Path("infinite-done")
   public String sseInfiniteDone() {
      return String.valueOf(terminated);
   }
}
