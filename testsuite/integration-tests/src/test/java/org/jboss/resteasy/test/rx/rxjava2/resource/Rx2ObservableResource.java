package org.jboss.resteasy.test.rx.rxjava2.resource;

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
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.Thing;

import io.reactivex.Observable;

public interface Rx2ObservableResource {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> get();

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> getThing();

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> getThingList();

   @GET
   @Path("get/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> getBytes();

   @PUT
   @Path("put/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> put(String s);

   @PUT
   @Path("put/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> putThing(String s);

   @PUT
   @Path("put/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> putThingList(String s);

   @PUT
   @Path("put/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> putBytes(String s);

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> post(String s);

   @POST
   @Path("post/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> postThing(String s);

   @POST
   @Path("post/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> postThingList(String s);

   @POST
   @Path("post/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> postBytes(String s);

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> delete();

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> deleteThing();

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> deleteThingList();

   @DELETE
   @Path("delete/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> deleteBytes();

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> head();

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> options();

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> optionsThing();

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> optionsThingList();

   @OPTIONS
   @Path("options/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> optionsBytes();

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   Observable<String> trace();

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<Thing> traceThing();

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   Observable<List<Thing>> traceThingList();

   @TRACE
   @Path("trace/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   Observable<byte[]> traceBytes();

   @GET
   @Path("exception/unhandled")
   Observable<Thing> exceptionUnhandled() throws Exception;

   @GET
   @Path("exception/handled")
   Observable<Thing> exceptionHandled() throws Exception;
}
