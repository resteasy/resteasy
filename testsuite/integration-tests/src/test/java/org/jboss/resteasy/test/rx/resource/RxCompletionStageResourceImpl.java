package org.jboss.resteasy.test.rx.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.rxjava2.SingleProvider;

import io.reactivex.Single;

@Path("")
public class RxCompletionStageResourceImpl {

    private static SingleProvider singleProvider = new SingleProvider();

    @SuppressWarnings("unchecked")
    @GET
    @Path("get/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> get() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("get/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> getThing() {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing("x")));
    }

    @GET
    @Path("get/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> getThingList() {
        return (CompletionStage<List<Thing>>) buildCompletionStageThingList("x", 3);
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("put/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> put(String s) {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just(s));
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("put/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> putThing(String s) {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing(s)));
    }

    @PUT
    @Path("put/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> putThingList(String s) {
        return (CompletionStage<List<Thing>>) buildCompletionStageThingList(s, 3);
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("post/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> post(String s) {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just(s));
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("post/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> postThing(String s) {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing(s)));
    }

    @POST
    @Path("post/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> postThingList(String s) {
        return (CompletionStage<List<Thing>>) buildCompletionStageThingList(s, 3);
    }

    @SuppressWarnings("unchecked")
    @DELETE
    @Path("delete/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> delete() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @SuppressWarnings("unchecked")
    @DELETE
    @Path("delete/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> deleteThing() {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing("x")));
    }

    @DELETE
    @Path("delete/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> deleteThingList() {
        return (CompletionStage<List<Thing>>) buildCompletionStageThingList("x", 3);
    }

    @SuppressWarnings("unchecked")
    @HEAD
    @Path("head/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> head() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @SuppressWarnings("unchecked")
    @OPTIONS
    @Path("options/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> options() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @SuppressWarnings("unchecked")
    @OPTIONS
    @Path("options/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> optionsThing() {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing("x")));
    }

    @OPTIONS
    @Path("options/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> optionsThingList() {
        return buildCompletionStageThingList("x", 3);
    }

    @SuppressWarnings("unchecked")
    @TRACE
    @Path("trace/string")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> trace() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @SuppressWarnings("unchecked")
    @TRACE
    @Path("trace/thing")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Thing> traceThing() {
        return (CompletionStage<Thing>) singleProvider.toCompletionStage(Single.just(new Thing("x")));
    }

    @TRACE
    @Path("trace/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<Thing>> traceThingList() {
        return buildCompletionStageThingList("x", 3);
    }

    @SuppressWarnings("unchecked")
    static CompletionStage<List<Thing>> buildCompletionStageThingList(String s, int listSize) {
        List<Thing> list = new ArrayList<Thing>();
        for (int i = 0; i < listSize; i++) {
            list.add(new Thing(s));
        }
        return (CompletionStage<List<Thing>>) singleProvider.toCompletionStage(Single.just(list));
    }

    @GET
    @Path("exception/unhandled")
    public CompletionStage<Thing> exceptionUnhandled() throws Exception {
        throw new Exception("unhandled");
    }

    @GET
    @Path("exception/handled")
    public CompletionStage<Thing> exceptionHandled() throws Exception {
        throw new TestException("handled");
    }

    @SuppressWarnings("unchecked")
    @FilterException
    @GET
    @Path("exception/filter")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> exceptionInFilter() {
        return (CompletionStage<String>) singleProvider.toCompletionStage(Single.just("x"));
    }

    @FilterException
    @GET
    @Path("exception/filter-sync")
    @Produces(MediaType.TEXT_PLAIN)
    public String exceptionInFilterSync() {
        return "x";
    }
}
