package org.jboss.resteasy.test.core.interceptors.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("dummyservice")
public class ClientResponseFilterExceptionResourceImpl {

   @Path("sync")
   @GET
   @Produces("text/plain")
   public String sync() {
      return "sync";
   }
   
   @Path("cs")
   @GET
   @Produces("text/plain")
   public CompletionStage<String> cs() {
      return CompletableFuture.completedFuture("cs");
   }
   
   @Path("single")
   @GET
   @Produces("text/plain")
   public Single<String> single() {
      return Single.just("single");
   }
   
   @Path("observable")
   @GET
   @Produces("text/plain")
   public Observable<String> observable() {
      return Observable.just("observable");
   }
   
   @Path("flowable")
   @GET
   @Produces("text/plain")
   public Flowable<String> flowable() {
      return Flowable.just("flowable");
   }
}
