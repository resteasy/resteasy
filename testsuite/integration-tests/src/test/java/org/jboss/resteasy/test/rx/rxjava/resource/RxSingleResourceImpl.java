package org.jboss.resteasy.test.rx.rxjava.resource;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.Thing;

import rx.Single;

@Path("")
public class RxSingleResourceImpl {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> get() throws InterruptedException {
      return Single.just("x");
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> getThing() throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing("x"));
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> getThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList("x", 3);
   }

   @PUT
   @Path("put/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> put(String s) throws InterruptedException {
      Thread.sleep(500);
      return Single.just(s);
   }

   @PUT
   @Path("put/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> putThing(String s) throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing(s));
   }

   @PUT
   @Path("put/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> putThingList(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList(s, 3);
   }

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> post(String s) throws InterruptedException {
      Thread.sleep(500);
      return Single.just(s);
   }

   @POST
   @Path("post/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> postThing(String s) throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing(s));
   }

   @POST
   @Path("post/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> postThingList(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList(s, 3);
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> delete() throws InterruptedException {
      Thread.sleep(500);
      return Single.just("x");
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> deleteThing() throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing("x"));
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> deleteThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList("x", 3);
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> head() throws InterruptedException {
      Thread.sleep(500);
      return Single.just("x");
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> options() throws InterruptedException {
      Thread.sleep(500);
      return Single.just("x");
   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> optionsThing() throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing("x"));
   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> optionsThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList("x", 3);
   }

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   public Single<String> trace() throws InterruptedException {
      Thread.sleep(500);
      return Single.just("x");
   }

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_XML)
   public Single<Thing> traceThing() throws InterruptedException {
      Thread.sleep(500);
      return Single.just(new Thing("x"));
   }

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   public Single<List<Thing>> traceThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildSingleThingList("x", 3);
   }
   
   @GET
   @Path("exception/unhandled")
   public Single<Thing> exceptionUnhandled() throws Exception {
      throw new Exception("unhandled");
   }
   
   @GET
   @Path("exception/handled")
   public Single<Thing> exceptionHandled() throws Exception {
      throw new TestException("handled");
   }

   static Single<List<Thing>> buildSingleThingList(String s, int listSize) {
      List<Thing> list = new ArrayList<Thing>();
      for (int i = 0; i < listSize; i++) {
         list.add(new Thing(s));
      }
      return Single.just(list);
   }
}
