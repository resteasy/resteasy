package org.jboss.resteasy.rxjava2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

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
   @GET
   @Stream
   public Observable<String> observable()
   {
      return Observable.fromArray("one", "two");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("flowable")
   @GET
   @Stream
   public Flowable<String> flowable()
   {
      return Flowable.fromArray("one", "two");
   }

   @Path("context/single")
   @GET
   public Single<String> contextSingle(@Context UriInfo uriInfo)
   {
      return Single.<String>create(foo -> {
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
   @GET
   @Stream
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
               foo.onComplete();
            }
         });
      }).map(str -> {
         uriInfo.getAbsolutePath();
         return str;
      });
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("context/flowable")
   @GET
   @Stream
   public Flowable<String> contextFlowable(@Context UriInfo uriInfo)
   {
      return Flowable.<String>create(foo -> {
         ExecutorService executor = Executors.newSingleThreadExecutor();
         executor.submit(new Runnable()
         {
            public void run()
            {
               foo.onNext("one");
               foo.onNext("two");
               foo.onComplete();
            }
         });
      }, BackpressureStrategy.BUFFER).map(str -> {
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
