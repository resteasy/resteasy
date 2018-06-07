package org.jboss.resteasy.test.rx.rxjava2.resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("/")
public class NoStreamRx2Resource
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
   public Observable<String> observable()
   {
      return Observable.fromArray("one", "two");
   }

   @Produces(MediaType.APPLICATION_JSON)
   @Path("flowable")
   @GET
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
}
