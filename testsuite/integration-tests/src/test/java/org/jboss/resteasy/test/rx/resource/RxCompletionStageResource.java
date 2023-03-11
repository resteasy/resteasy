package org.jboss.resteasy.test.rx.resource;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public interface RxCompletionStageResource {

    @GET
    @Path("get/string")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> get();

    @GET
    @Path("get/thing")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> getThing();

    @GET
    @Path("get/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> getThingList();

    @PUT
    @Path("put/string")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> put(String s);

    @PUT
    @Path("put/thing")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> putThing(String s);

    @PUT
    @Path("put/thing/list")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> putThingList(String s);

    @POST
    @Path("post/string")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> post(String s);

    @POST
    @Path("post/thing")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> postThing(String s);

    @POST
    @Path("post/thing/list")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> postThingList(String s);

    @DELETE
    @Path("delete/string")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> delete();

    @DELETE
    @Path("delete/thing")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> deleteThing();

    @DELETE
    @Path("delete/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> deleteThingList();

    @HEAD
    @Path("head/string")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> head();

    @OPTIONS
    @Path("options/string")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> options();

    @OPTIONS
    @Path("options/thing")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> optionsThing();

    @OPTIONS
    @Path("options/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> optionsThingList();

    @TRACE
    @Path("trace/string")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> trace();

    @TRACE
    @Path("trace/thing")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<Thing> traceThing();

    @TRACE
    @Path("trace/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<List<Thing>> traceThingList();

    @GET
    @Path("exception/unhandled")
    CompletionStage<Thing> exceptionUnhandled() throws Exception;

    @GET
    @Path("exception/handled")
    CompletionStage<Thing> exceptionHandled() throws Exception;

    @GET
    @Path("exception/filter")
    @Produces(MediaType.TEXT_PLAIN)
    CompletionStage<String> exceptionInFilter();
}
