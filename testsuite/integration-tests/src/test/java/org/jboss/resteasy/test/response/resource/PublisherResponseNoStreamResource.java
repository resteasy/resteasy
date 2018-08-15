package org.jboss.resteasy.test.response.resource;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@Path("")
public class PublisherResponseNoStreamResource {

   private static boolean terminated = false;
   private static final Logger LOG = Logger.getLogger(PublisherResponseNoStreamResource.class);

   @GET
   @Path("text")
   @Produces("application/json")
   public Publisher<String> text(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback());
	   return Flowable.fromArray("one", "two");
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
   @Path("callback-called-no-error")
   public String callbackCalledNoError() {
      AsyncResponseCallback.assertCalled(false);
      return "OK";
   }

   @GET
   @Path("text-error-immediate")
   @Produces("application/json")
   public Publisher<String> textErrorImmediate(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback());
      throw new AsyncResponseException();
   }

   @GET
   @Path("text-error-deferred")
   @Produces("application/json")
   public Publisher<String> textErrorDeferred(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback());
      return Flowable.error(new AsyncResponseException());
   }

   @GET
   @Path("callback-called-with-error")
   public String callbackCalledWithError() {
      AsyncResponseCallback.assertCalled(true);
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
