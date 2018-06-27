package org.jboss.resteasy.test.core.interceptors.resource;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("dummyservice")
public interface ClientResponseFilterExceptionResource {

   @Path("sync")
   @GET
   public String sync();
   
   @Path("cs")
   @GET
   CompletionStage<String> cs();
   
   @Path("single")
   @GET
   Single<String> single();
   
   @Path("observable")
   @GET
   Observable<String> observable();
   
   @Path("flowable")
   @GET
   Flowable<String> flowable();
}
