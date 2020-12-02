package org.jboss.resteasy.test.core.interceptors.resource;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Path("dummyservice")
public interface ClientResponseFilterExceptionResource {

   @Path("sync")
   @GET
   @Produces("text/plain")
   String sync();

   @Path("cs")
   @GET
   @Produces("text/plain")
   CompletionStage<String> cs();

   @Path("single")
   @GET
   @Produces("text/plain")
   Single<String> single();

   @Path("observable")
   @GET
   @Produces("text/plain")
   Observable<String> observable();

   @Path("flowable")
   @GET
   @Produces("text/plain")
   Flowable<String> flowable();
}
