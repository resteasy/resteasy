package org.jboss.resteasy.rxjava;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Stream;

import rx.Emitter.BackpressureMode;
import rx.Observable;
import rx.Single;

@Path("/")
public class RxResource
{

   @Path("single")
   @GET
   public Single<String> single()
   {
      return Single.just("got it");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("observable")
   @Stream
   @GET
   public Observable<String> observable()
   {
      return Observable.from(new String[]
      {"one", "two"});
   }

   @Path("context/single")
   @GET
   public Single<String> contextSingle(@Context UriInfo uriInfo)
   {
      return Single.<String>fromEmitter(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onSuccess("got it");
            }
         });
      }).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("context/observable")
   @Stream
   @GET
   public Observable<String> contextObservable(@Context UriInfo uriInfo)
   {
      return Observable.<String>create(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onNext("one");
               foo.onNext("two");
               foo.onCompleted();
            }
         });
      }, BackpressureMode.BUFFER).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Path("injection")
   @GET
   public Single<Integer> injection(@Context Integer value)
   {
      return Single.just(value);
   }

   @Path("injection-async")
   @GET
   public Single<Integer> injectionAsync(@Async @Context Integer value)
   {
      return Single.just(value);
   }
}
