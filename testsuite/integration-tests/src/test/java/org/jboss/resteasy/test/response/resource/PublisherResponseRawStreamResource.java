package org.jboss.resteasy.test.response.resource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Stream;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@Path("")
public class PublisherResponseRawStreamResource {

   private static boolean terminated = false;

   @GET
   @Path("chunked")
   @Produces("application/json")
   @Stream(Stream.MODE.RAW)
   public Publisher<String> chunked() {
      return Flowable.fromArray("one", "two");
   }

   @GET
   @Path("chunked-infinite")
   @Produces("application/json")
   @Stream(Stream.MODE.RAW)
   public Publisher<String> chunkedInfinite() {
      terminated = false;
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
   @Path("infinite-done")
   public String sseInfiniteDone() {
      return String.valueOf(terminated);
   }
}
