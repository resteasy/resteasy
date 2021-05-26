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

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.Thing;

@Path("")
public class RSOPublisherBuilderResourceImpl {

   private static final String[] X_ARRAY = buildArrayString("x", 3);
   private static final Thing[] THING_X_ARRAY = buildArrayThing("x", 3);
   private static final List<Thing>[] LIST_THING_X_ARRAY = buildArrayListThing("x", 3, 2);
   private static final byte[][] BYTE_ARRAY_ARRAY = buildArrayArrayBytes(3);

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> get() {
      return ReactiveStreams.of(X_ARRAY);
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> getThing() {
      return ReactiveStreams.of(THING_X_ARRAY);
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> getThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY);
   }

   @GET
   @Path("get/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> getBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY);
   }

   @PUT
   @Path("put/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> put(String s) {
      return ReactiveStreams.of(buildArrayString(s, 3));
   }

   @PUT
   @Path("put/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> putThing(String s) {
      return ReactiveStreams.of(buildArrayThing(s, 3));
   }

   @PUT
   @Path("put/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> putThingList(String s) {
      return ReactiveStreams.of(buildArrayListThing(s, 3, 2));
   }

   @PUT
   @Path("put/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> putBytes(String s) {
      return ReactiveStreams.of(buildArrayArrayBytes(Integer.valueOf(s)));
   }

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> post(String s) {
      return ReactiveStreams.of(buildArrayString(s, 3));
   }

   @POST
   @Path("post/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> postThing(String s) {
      return ReactiveStreams.of(buildArrayThing(s, 3));
   }

   @POST
   @Path("post/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> postThingList(String s) {
      return ReactiveStreams.of(buildArrayListThing(s, 3, 2));
   }

   @POST
   @Path("post/bytes")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> postBytes(String s) {
      return ReactiveStreams.of(buildArrayArrayBytes(Integer.valueOf(s)));
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> delete() {
      return ReactiveStreams.of(X_ARRAY);
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> deleteThing() {
      return ReactiveStreams.of(THING_X_ARRAY);
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> deleteThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY);
   }

   @DELETE
   @Path("delete/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> deleteBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY);
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> head() {
      return ReactiveStreams.of(X_ARRAY);
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> options() {
      return ReactiveStreams.of(X_ARRAY);
   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> optionsThing() {
      return ReactiveStreams.of(THING_X_ARRAY);
   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> optionsThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY);
   }

   @OPTIONS
   @Path("options/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> optionsBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY);
   }

   @TRACE
   @Path("trace/string")
   @Produces(MediaType.TEXT_PLAIN)
   @Stream
   public PublisherBuilder<String> trace() {
      return ReactiveStreams.of(X_ARRAY);
   }

   @TRACE
   @Path("trace/thing")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<Thing> traceThing() {
      return ReactiveStreams.of(THING_X_ARRAY);
   }

   @TRACE
   @Path("trace/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   @Stream
   public PublisherBuilder<List<Thing>> traceThingList() {
      return ReactiveStreams.of(LIST_THING_X_ARRAY);
   }

   @TRACE
   @Path("trace/bytes")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Stream
   public PublisherBuilder<byte[]> traceBytes() {
      return ReactiveStreams.of(BYTE_ARRAY_ARRAY);
   }

   @GET
   @Path("exception/unhandled")
   public PublisherBuilder<Thing> exceptionUnhandled() throws Exception {
      throw new Exception("unhandled");
   }

   @GET
   @Path("exception/handled")
   public PublisherBuilder<Thing> exceptionHandled() throws Exception {
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
