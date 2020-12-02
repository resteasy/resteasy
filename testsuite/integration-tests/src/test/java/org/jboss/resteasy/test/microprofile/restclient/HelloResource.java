package org.jboss.resteasy.test.microprofile.restclient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.Assert;

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
       Assert.assertNull(nonPropagatedHeader);
       Assert.assertEquals("got-a-value", propagatedHeader);
       return CompletableFuture.completedFuture("OK");
    }

    @GET
    @Path("async-client")
    public CompletionStage<String> asyncClient(@HeaderParam("X-Propagated") String propagatedHeader,
                                               @HeaderParam("X-Not-Propagated") String nonPropagatedHeader){
       Assert.assertEquals("got-a-value", propagatedHeader);
       Assert.assertEquals("got-a-value", nonPropagatedHeader);
       return rest.asyncClientTarget();
    }

    @GET
    @Path("client-target")
    public String clientTarget(@HeaderParam("X-Propagated") String propagatedHeader,
                               @HeaderParam("X-Not-Propagated") String nonPropagatedHeader) {
       Assert.assertNull(nonPropagatedHeader);
       Assert.assertEquals("got-a-value", propagatedHeader);
       return "OK";
    }

    @GET
    @Path("client")
    public String client(@HeaderParam("X-Propagated") String propagatedHeader,
                         @HeaderParam("X-Not-Propagated") String nonPropagatedHeader){
       Assert.assertEquals("got-a-value", propagatedHeader);
       Assert.assertEquals("got-a-value", nonPropagatedHeader);
       return rest.clientTarget();
    }

    @GET
    @Path("async-client-404")
    public CompletionStage<String> asyncClient404(){
       return rest.asyncClient404Target();
    }
}