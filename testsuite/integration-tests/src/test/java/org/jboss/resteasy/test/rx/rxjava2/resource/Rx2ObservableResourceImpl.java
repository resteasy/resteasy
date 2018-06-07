package org.jboss.resteasy.test.rx.rxjava2.resource;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


@Path("")
public class Rx2ObservableResourceImpl {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> get() {
      return buildObservableString("x", 3);
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> getThing() {
      return buildObservableThing("x", 3);
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> getThingList() {
      return buildObservableThingList("x", 2, 3);
   }

   @GET
   @Path("get/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> getBytes() {
      return buildObservableBytes(3);
   }

   @PUT
   @Path("put/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> put(String s) {
      return buildObservableString(s, 3);
   }

   @PUT
   @Path("put/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> putThing(String s) {
      return buildObservableThing(s, 3);
   }

   @PUT
   @Path("put/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> putThingList(String s) {
      return buildObservableThingList(s, 2, 3);
   }

   @PUT
   @Path("put/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> putBytes(String s) {
      int n = Integer.valueOf(s);
      return buildObservableBytes(n);
   }

   @POST
   @Path("post/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> post(String s) {
      return buildObservableString(s, 3);
   }

   @POST
   @Path("post/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> postThing(String s) {
      return buildObservableThing(s, 3);
   }

   @POST
   @Path("post/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> postThingList(String s) {
      return buildObservableThingList(s, 2, 3);
   }

   @POST
   @Path("post/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> postBytes(String s) {
      int n = Integer.valueOf(s);
      return buildObservableBytes(n);
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> delete() {
      return buildObservableString("x", 3);
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> deleteThing() {
      return buildObservableThing("x", 3);
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> deleteThingList() {
      return buildObservableThingList("x", 2, 3);
   }

   @DELETE
   @Path("delete/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> deleteBytes() {
      return buildObservableBytes(3);
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> head() {
      return buildObservableString("x", 3);
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> options() {
      return buildObservableString("x", 3);
   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> optionsThing() {
      return buildObservableThing("x", 3);
   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> optionsThingList() {
      return buildObservableThingList("x", 2, 3);
   }

   @OPTIONS
   @Path("options/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> optionsBytes() {
      return buildObservableBytes(3);
   }

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> trace() {
      return buildObservableString("x", 3);
   }

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> traceThing() {
      return buildObservableThing("x", 3);
   }

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> traceThingList() {
      return buildObservableThingList("x", 2, 3);
   }

   @TRACE
   @Path("trace/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Observable<byte[]> traceBytes() {
      return buildObservableBytes(3);
   }

   @GET
   @Path("exception/unhandled")
   public Observable<Thing> exceptionUnhandled() throws Exception {
      throw new Exception("unhandled");
   }

   @GET
   @Path("exception/handled")
   public Observable<Thing> exceptionHandled() throws Exception {
      throw new TestException("handled");
   }

   static <T> Observable<String> buildObservableString(String s, int n) {
      return Observable.create(
         new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
               for (int i = 0; i < n; i++)   {
                  emitter.onNext(s);
               }
               emitter.onComplete();
            }
         });
   }

   static Observable<Thing> buildObservableThing(String s, int n) {
      return Observable.create(
         new ObservableOnSubscribe<Thing>() {

            @Override
            public void subscribe(ObservableEmitter<Thing> emitter) throws Exception {
               for (int i = 0; i < n; i++) {
                  emitter.onNext(new Thing(s));
               }
               emitter.onComplete();
            }
         });
   }

   static Observable<List<Thing>> buildObservableThingList(String s, int listSize, int elementSize) {
      return Observable.create(
         new ObservableOnSubscribe<List<Thing>>() {

            @Override
            public void subscribe(ObservableEmitter<List<Thing>> emitter) throws Exception {
               for (int i = 0; i < listSize; i++) {
                  List<Thing> list = new ArrayList<Thing>();
                  for (int j = 0; j < elementSize; j++) {
                     list.add(new Thing(s));
                  }
                  emitter.onNext(list);
               }
               emitter.onComplete();
            }
         });
   }

   static Observable<byte[]> buildObservableBytes(int n) {
      return Observable.create(
         new ObservableOnSubscribe<byte[]>() {

            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
               for (int i = 0; i < n; i++) {
                  emitter.onNext(Bytes.BYTES);
               }
               emitter.onComplete();
            }
         });
   }
}