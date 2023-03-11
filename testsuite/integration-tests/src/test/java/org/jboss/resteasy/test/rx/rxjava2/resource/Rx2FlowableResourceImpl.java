package org.jboss.resteasy.test.rx.rxjava2.resource;

import java.util.ArrayList;
import java.util.List;

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

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

@Path("")
public class Rx2FlowableResourceImpl {

    @GET
    @Path("get/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> get() {
        return buildFlowableString("x", 3);
    }

    @GET
    @Path("get/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> getThing() {
        return buildFlowableThing("x", 3);
    }

    @GET
    @Path("get/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> getThingList() {
        return buildFlowableThingList("x", 2, 3);
    }

    @GET
    @Path("get/bytes")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> getBytes() {
        return buildFlowableBytes(3);
    }

    @PUT
    @Path("put/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> put(String s) {
        return buildFlowableString(s, 3);
    }

    @PUT
    @Path("put/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> putThing(String s) {
        return buildFlowableThing(s, 3);
    }

    @PUT
    @Path("put/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> putThingList(String s) {
        return buildFlowableThingList(s, 2, 3);
    }

    @PUT
    @Path("put/bytes")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> putBytes(String s) {
        int n = Integer.valueOf(s);
        return buildFlowableBytes(n);
    }

    @POST
    @Path("post/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> post(String s) {
        return buildFlowableString(s, 3);
    }

    @POST
    @Path("post/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> postThing(String s) {

        return buildFlowableThing(s, 3);
    }

    @POST
    @Path("post/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> postThingList(String s) {
        return buildFlowableThingList(s, 2, 3);
    }

    @POST
    @Path("post/bytes")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> postBytes(String s) {
        int n = Integer.valueOf(s);
        return buildFlowableBytes(n);
    }

    @DELETE
    @Path("delete/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> delete() {
        return buildFlowableString("x", 3);
    }

    @DELETE
    @Path("delete/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> deleteThing() {
        return buildFlowableThing("x", 3);
    }

    @DELETE
    @Path("delete/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> deleteThingList() {
        return buildFlowableThingList("x", 2, 3);
    }

    @DELETE
    @Path("delete/bytes")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> deleteBytes() {
        return buildFlowableBytes(3);
    }

    @HEAD
    @Path("head/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> head() {
        return buildFlowableString("x", 3);
    }

    @OPTIONS
    @Path("options/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> options() {
        return buildFlowableString("x", 3);
    }

    @OPTIONS
    @Path("options/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> optionsThing() {
        return buildFlowableThing("x", 3);
    }

    @OPTIONS
    @Path("options/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> optionsThingList() {
        return buildFlowableThingList("x", 2, 3);
    }

    @OPTIONS
    @Path("options/bytes")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> optionsBytes() {
        return buildFlowableBytes(3);
    }

    @TRACE
    @Path("trace/string")
    @Produces(MediaType.TEXT_PLAIN)
    @Stream
    public Flowable<String> trace() {
        return buildFlowableString("x", 3);
    }

    @TRACE
    @Path("trace/thing")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<Thing> traceThing() {
        return buildFlowableThing("x", 3);
    }

    @TRACE
    @Path("trace/thing/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Stream
    public Flowable<List<Thing>> traceThingList() {
        return buildFlowableThingList("x", 2, 3);
    }

    @TRACE
    @Path("trace/bytes")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Stream
    public Flowable<byte[]> traceBytes() {
        return buildFlowableBytes(3);
    }

    @GET
    @Path("exception/unhandled")
    public Flowable<Thing> exceptionUnhandled() throws Exception {
        throw new Exception("unhandled");
    }

    @GET
    @Path("exception/handled")
    public Flowable<Thing> exceptionHandled() throws Exception {
        throw new TestException("handled");
    }

    static <T> Flowable<String> buildFlowableString(String s, int n) {
        return Flowable.create(
                new FlowableOnSubscribe<String>() {

                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        for (int i = 0; i < n; i++) {
                            emitter.onNext(s);
                        }
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }

    static Flowable<Thing> buildFlowableThing(String s, int n) {
        return Flowable.create(
                new FlowableOnSubscribe<Thing>() {

                    @Override
                    public void subscribe(FlowableEmitter<Thing> emitter) throws Exception {
                        for (int i = 0; i < n; i++) {
                            emitter.onNext(new Thing(s));
                        }
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }

    static Flowable<List<Thing>> buildFlowableThingList(String s, int listSize, int elementSize) {
        return Flowable.create(
                new FlowableOnSubscribe<List<Thing>>() {

                    @Override
                    public void subscribe(FlowableEmitter<List<Thing>> emitter) throws Exception {
                        for (int i = 0; i < listSize; i++) {
                            List<Thing> list = new ArrayList<Thing>();
                            for (int j = 0; j < elementSize; j++) {
                                list.add(new Thing(s));
                            }
                            emitter.onNext(list);
                        }
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }

    static Flowable<byte[]> buildFlowableBytes(int n) {
        return Flowable.create(
                new FlowableOnSubscribe<byte[]>() {

                    @Override
                    public void subscribe(FlowableEmitter<byte[]> emitter) throws Exception {
                        for (int i = 0; i < n; i++) {
                            emitter.onNext(Bytes.BYTES);
                        }
                        emitter.onComplete();
                    }
                },
                BackpressureStrategy.BUFFER);
    }
}
