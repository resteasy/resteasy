package org.jboss.resteasy.test.rx.rso.resource;


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

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.reactivestreams.Publisher;

@Path("")
public class RSOPublisherResourceImpl {

   private static final String[] X_ARRAY = buildArrayString("x", 3);
   private static final Thing[] THING_X_ARRAY = buildArrayThing("x", 3);
   private static final List<Thing>[] LIST_THING_X_ARRAY = buildArrayListThing("x", 3, 2);
   private static final byte[][] BYTE_ARRAY_ARRAY = buildArrayArrayBytes(3);

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> get() {
      return ReactiveStreams.of(X_ARRAY).buildRs();
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> getThing() {
      return ReactiveStreams.of(THING_X_ARRAY).buildRs();
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> getThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY).buildRs();
   }

   @GET
   @Path("get/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> getBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY).buildRs();
   }

   @PUT
   @Path("put/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> put(String s) {
      return ReactiveStreams.of(buildArrayString(s, 3)).buildRs();
   }

   @PUT
   @Path("put/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> putThing(String s) {
      return ReactiveStreams.of(buildArrayThing(s, 3)).buildRs();
   }

   @PUT
   @Path("put/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> putThingList(String s) {
      return ReactiveStreams.of(buildArrayListThing(s, 3, 2)).buildRs();
   }

   @PUT
   @Path("put/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> putBytes(String s) {
      return ReactiveStreams.of(buildArrayArrayBytes(Integer.valueOf(s))).buildRs();
   }

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> post(String s) {
      return ReactiveStreams.of(buildArrayString(s, 3)).buildRs();
   }

   @POST
   @Path("post/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> postThing(String s) {
      return ReactiveStreams.of(buildArrayThing(s, 3)).buildRs();
   }

   @POST
   @Path("post/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> postThingList(String s) {
      return ReactiveStreams.of(buildArrayListThing(s, 3, 2)).buildRs();
   }

   @POST
   @Path("post/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> postBytes(String s) {
      return ReactiveStreams.of(buildArrayArrayBytes(Integer.valueOf(s))).buildRs();
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> delete() {
      return ReactiveStreams.of(X_ARRAY).buildRs();
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> deleteThing() {
      return ReactiveStreams.of(THING_X_ARRAY).buildRs();
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> deleteThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY).buildRs();
   }

   @DELETE
   @Path("delete/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> deleteBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY).buildRs();
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> head() {
      return ReactiveStreams.of(X_ARRAY).buildRs();
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> options() {
      return ReactiveStreams.of(X_ARRAY).buildRs();
   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> optionsThing() {
      return ReactiveStreams.of(THING_X_ARRAY).buildRs();
   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> optionsThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY).buildRs();
   }

   @OPTIONS
   @Path("options/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> optionsBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY).buildRs();
   }

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public Publisher<String> trace() {
      return ReactiveStreams.of(X_ARRAY).buildRs();
   }

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<Thing> traceThing() {
      return ReactiveStreams.of(THING_X_ARRAY).buildRs();
   }

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public Publisher<List<Thing>> traceThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY).buildRs();
   }

   @TRACE
   @Path("trace/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public Publisher<byte[]> traceBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY).buildRs();
   }

   @GET
   @Path("exception/unhandled")
   public Publisher<Thing> exceptionUnhandled() throws Exception {
      throw new Exception("unhandled");
   }

   @GET
   @Path("exception/handled")
   public Publisher<Thing> exceptionHandled() throws Exception {
      throw new TestException("handled");
   }

   static String[] buildArrayString(String s, int n) {
      String[] strings  = new String[n];
      for (int i = 0; i < n; i++) {
         strings[i] = s;
      }
      return strings;
   }

   static Thing[] buildArrayThing(String s, int n) {
      Thing[] things  = new Thing[n];
      Thing thing = new Thing(s);
      for (int i = 0; i < n; i++) {
         things[i] = thing;
      }
      return things;
   }

   static ArrayList<Thing> buildListThing(String s, int n) {
      ArrayList<Thing> list = new ArrayList<Thing>();
      Thing thing = new Thing(s);
      for (int i = 0; i < n; i++) {
         list.add(thing);
      }
      return list;
   }

   static List<Thing>[] buildArrayListThing(String s, int n, int m) {
      ArrayList<Thing> list = buildListThing(s, n);
      @SuppressWarnings("unchecked")
      ArrayList<Thing>[] array = new ArrayList[m];
      for (int i = 0; i < m; i++) {
         array[i] = list;
      }
      return array;
   }

   static byte[][] buildArrayArrayBytes(int n) {
      byte[][] bytes = new byte[n][Bytes.BYTES.length];
      for (int i = 0; i < n; i++) {
         bytes[i] = Bytes.BYTES;
      }
      return bytes;
   }
}
