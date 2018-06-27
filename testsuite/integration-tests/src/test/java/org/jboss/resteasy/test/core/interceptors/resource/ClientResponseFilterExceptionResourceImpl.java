package org.jboss.resteasy.test.core.interceptors.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("dummyservice")
public class ClientResponseFilterExceptionResourceImpl {

   @Path("sync")
   @GET
   public String dummy() {
      return "sync";
   }
   
   @Path("cs")
   @GET
   CompletionStage<String> cs() {
      return CompletableFuture.completedFuture("cs");
   }
   
   @Path("single")
   @GET
   Single<String> single() {
      return Single.just("single");
   }
   
   @Path("observable")
   @GET
   Observable<String> observable() {
      return Observable.just("observable");
   }
   
   @Path("flowable")
   @GET
   Flowable<String> flowable() {
      return Flowable.just("flowable");
   }
}
