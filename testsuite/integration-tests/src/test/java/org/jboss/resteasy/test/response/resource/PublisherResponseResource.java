package org.jboss.resteasy.test.response.resource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.spi.HttpRequest;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@Path("")
public class PublisherResponseResource {

   private static boolean terminated = false;
   
   @GET
   @Path("text")
   @Produces("application/json")
   @Stream
   public Publisher<String> text(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback());
      return Flowable.fromArray("one", "two");
   }

   @GET
   @Path("text-infinite")
   @Produces("application/json")
   @Stream
   public Publisher<String> textInfinite() {
      terminated = false;
      System.err.println("Starting ");
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
   @Stream
   public Publisher<String> textErrorImmediate(@Context HttpRequest req) {
      req.getAsyncContext().getAsyncResponse().register(new AsyncResponseCallback());
      throw new AsyncResponseException();
   }

   @GET
   @Path("text-error-deferred")
   @Produces("application/json")
   @Stream
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

   @Stream
   @GET
   @Path("chunked")
   @Produces("application/json")
   public Publisher<String> chunked() {
      return Flowable.fromArray("one", "two");
   }

   @Stream
   @GET
   @Path("chunked-infinite")
   @Produces("application/json")
   public Publisher<String> chunkedInfinite() {
      terminated = false;
      System.err.println("Starting ");
      char[] chunk = new char[8192];
      Arrays.fill(chunk, 'a');
      String ret = new String(chunk);
      return Flowable.interval(1, TimeUnit.SECONDS).map(v -> {
         return ret;
      }).doFinally(() -> {
         terminated = true;
      });
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
