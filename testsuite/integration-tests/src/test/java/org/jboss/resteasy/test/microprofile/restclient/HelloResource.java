package org.jboss.resteasy.test.microprofile.restclient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.reactivex.Single;

@Path("/")
public class HelloResource {

   @Inject
   @RestClient
   HelloClient rest;

    @GET
    @Produces("text/plain")
    @Path("/hello")
    public String hello() {
       return "Hello";
    }

    @GET
    @Produces("text/plain")
    @Path("/null-path-param/{value}")
    public String nullPathParam(@PathParam("value") final String value) {
        return value;
    }

    @GET
    @Path("/null-query-param/")
    public String nullQueryParam(@QueryParam("value") String value) {
        return value;
    }

    @GET
    @Path("/some/{id}")
    public Single<String> single(@PathParam("id") String id) {
       return Single.just(id);
    }

    @GET
    @Path("/cs/{id}")
    public CompletionStage<String> cs(@PathParam("id") String id) {
       return CompletableFuture.completedFuture(id);
    }

    @GET
    @Path("async-client-target")
    public CompletionStage<String> asyncClientTarget(@HeaderParam("X-Propagated") String propagatedHeader,
                                                @HeaderParam("X-Not-Propagated") String nonPropagatedHeader) {
        if (nonPropagatedHeader != null) {
            throw new RuntimeException("nonPropagatedHeader is not null");
        }
        if (!propagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("propagatedHeader is not \"got-a-value\"");
        }
       return CompletableFuture.completedFuture("OK");
    }

    @GET
    @Path("async-client")
    public CompletionStage<String> asyncClient(@HeaderParam("X-Propagated") String propagatedHeader,
                                               @HeaderParam("X-Not-Propagated") String nonPropagatedHeader){
        if (!propagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("propagatedHeader is not \"got-a-value\"");
        }
        if (!nonPropagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("nonPropagatedHeader is not \"got-a-value\"");
        }
       return rest.asyncClientTarget();
    }

    @GET
    @Path("client-target")
    public String clientTarget(@HeaderParam("X-Propagated") String propagatedHeader,
                               @HeaderParam("X-Not-Propagated") String nonPropagatedHeader) {
        if (nonPropagatedHeader != null) {
            throw new RuntimeException("nonPropagatedHeader is not null");
        }
        if (!propagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("propagatedHeader is not \"got-a-value\"");
        }
       return "OK";
    }

    @GET
    @Path("client")
    public String client(@HeaderParam("X-Propagated") String propagatedHeader,
                         @HeaderParam("X-Not-Propagated") String nonPropagatedHeader){
        if (!propagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("propagatedHeader is not \"got-a-value\"");
        }
        if (!nonPropagatedHeader.equals("got-a-value")) {
            throw new RuntimeException("nonPropagatedHeader is not \"got-a-value\"");
        }
       return rest.clientTarget();
    }

    @GET
    @Path("async-client-404")
    public CompletionStage<String> asyncClient404(){
       return rest.asyncClient404Target();
    }
}