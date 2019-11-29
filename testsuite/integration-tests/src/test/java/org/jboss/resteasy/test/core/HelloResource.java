package org.jboss.resteasy.test.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.reactivex.Single;

@Path("/")
public class HelloResource {

    @GET
    @Produces("text/plain")
    @Path("/hello")
    public String hello() {
       return "Hello" + ResteasyProviderFactory.peekInstance();
    }

    @GET
    @Path("/some/{id}")
    public Single<String> single(@PathParam("id") String id) {
       return Single.just(id);
    }

    @GET
    @Path("/some/{id}")
    public CompletionStage<String> cs(@PathParam("id") String id) {
       return CompletableFuture.completedFuture(id);
    }
}