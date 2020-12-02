package org.jboss.resteasy.test.response.resource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.core.ResteasyContext;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

@Path("")
public class PublisherResponseRawStreamResource {

   private static boolean terminated = false;

   @GET
   @Path("chunked")
   @Produces("application/json")
   @Stream(Stream.MODE.RAW)
   public Publisher<String> chunked() {
      return Flowable.create(source ->{
         for(int i=0;i<30;i++) {
            source.onNext(i+"-"+ResteasyContext.getContextDataLevelCount());
         }
         source.onComplete();
      }, BackpressureStrategy.BUFFER);
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

   @GET
   @Path("slow-async-io")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream(Stream.MODE.RAW)
   public Publisher<SlowString> slowAsyncWriter() {
      return Flowable.fromArray(new SlowString("one"), new SlowString("two"));
   }
}
