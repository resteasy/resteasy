package org.jboss.resteasy.test.rx.resource;

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

import rx.Observable;

public interface RxObservableResource {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> get() throws InterruptedException;
   
   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> getThing() throws InterruptedException;

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> getThingList() throws InterruptedException;

   @PUT
   @Path("put/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> put(String s) throws InterruptedException;

   @PUT
   @Path("put/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> putThing(String s) throws InterruptedException;
   
   @PUT
   @Path("put/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> putThingList(String s) throws InterruptedException;
   
   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> post(String s) throws InterruptedException;

   @POST
   @Path("post/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> postThing(String s) throws InterruptedException;
   
   @POST
   @Path("post/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> postThingList(String s) throws InterruptedException;
   
   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> delete() throws InterruptedException;

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> deleteThing() throws InterruptedException;
   
   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> deleteThingList() throws InterruptedException;
   
   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> head() throws InterruptedException;
   
   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> options() throws InterruptedException;
   
   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> optionsThing() throws InterruptedException;

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> optionsThingList() throws InterruptedException;
   
   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Observable<String> trace() throws InterruptedException;
   
   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<Thing> traceThing() throws InterruptedException;

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_XML)
   @Stream
   public Observable<List<Thing>> traceThingList() throws InterruptedException;
}
