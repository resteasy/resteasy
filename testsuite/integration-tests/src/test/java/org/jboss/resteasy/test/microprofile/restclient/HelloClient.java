package org.jboss.resteasy.test.microprofile.restclient;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import io.reactivex.Single;

@Path("/")
public interface HelloClient {

    @GET
    @Path("/hello")
    String hello();

    @GET
    @Path("some/{id}")
    Single<String> single(@PathParam("id") String id);

    @GET
    @Path("some/{id}")
    CompletionStage<String> cs(@PathParam("id") String id);
}