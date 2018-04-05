package org.jboss.resteasy.test.rx.resource;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;

import rx.Observable;
import rx.Subscriber;


/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created August 14, 2013
 */
@Path("")
public class RxObservableResourceImpl {

   @HttpMethod("TRACE")
   @Target(value = ElementType.METHOD)
   @Retention(value = RetentionPolicy.RUNTIME)
   public @interface TRACE {
   }

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> get() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString("x", 3);
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> getThing() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing("x", 3);
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> getThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList("x", 2, 3);
   }

   @PUT
   @Path("put/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> put(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString(s, 3);
   }

   @PUT
   @Path("put/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> putThing(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing(s, 3);
   }

   @PUT
   @Path("put/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> putThingList(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList(s, 2, 3);
   }

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> post(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString(s, 3);
   }

   @POST
   @Path("post/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> postThing(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing(s, 3);
   }

   @POST
   @Path("post/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> postThingList(String s) throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList(s, 2, 3);
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> delete() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString("x", 3);
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> deleteThing() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing("x", 3);
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> deleteThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList("x", 2, 3);
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> head() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString("x", 3);
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> options() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString("x", 3);

   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> optionsThing() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing("x", 3);

   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> optionsThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList("x", 2, 3);
   }

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> trace() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableString("x", 3);

   }

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<Thing> traceThing() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThing("x", 3);

   }

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Observable<List<Thing>> traceThingList() throws InterruptedException {
      Thread.sleep(500);
      return buildObservableThingList("x", 2, 3);
   }

   @SuppressWarnings("deprecation")
   static Observable<String> buildObservableString(String s, int n) {
      return Observable.create(
         new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> t) {
               try {
                  Thread.sleep(500);
               }
               catch (InterruptedException e) {
                  e.printStackTrace();
               }
               for (int i = 0; i < n; i++)
               {
                  t.onNext(s);
               }
            }
         });
   }

   @SuppressWarnings("deprecation")
   static Observable<Thing> buildObservableThing(String s, int n) {
      return Observable.create(
         new Observable.OnSubscribe<Thing>() {

            @Override
            public void call(Subscriber<? super Thing> t) {
               try {
                  Thread.sleep(500);
               }
               catch (InterruptedException e) {
                  e.printStackTrace();
               }
               for (int i = 0; i < n; i++)
               {
                  t.onNext(new Thing(s));
               }
            }
         });
   }

   @SuppressWarnings("deprecation")
   static Observable<List<Thing>> buildObservableThingList(String s, int listSize, int elementSize) {
      return Observable.create(
         new Observable.OnSubscribe<List<Thing>>() {

            @Override
            public void call(Subscriber<? super List<Thing>> t) {
               try {
                  Thread.sleep(500);
               }
               catch (InterruptedException e) {
                  e.printStackTrace();
               }
               for (int i = 0; i < listSize; i++) {
                  List<Thing> list = new ArrayList<Thing>();
                  for (int j = 0; j < elementSize; j++) {
                     list.add(new Thing(s));
                  }
                  t.onNext(list);
               }
            }
         });
   }
}