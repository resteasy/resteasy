package org.jboss.resteasy.test.rx.resource;

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


@Path("")
public class SimpleResourceImpl {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String get() {
      return "x";
   }

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing getThing() {
      return new Thing("x");
   }

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Thing> getThingList() {
      return buildThingList("x", 3);
   }

   @PUT
   @Path("put/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String put(String s) {
      return s;
   }

   @PUT
   @Path("put/thing")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing putThing(String s) {
      return new Thing(s);
   }

   @PUT
   @Path("put/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Thing> putThingList(String s) {
      return buildThingList(s, 3);
   }

   @POST
   @Path("post/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String post(String s) {
      return s;
   }

   @POST
   @Path("post/thing")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing postThing(String s) {
      return new Thing(s);
   }

   @POST
   @Path("post/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Thing> postThingList(String s) {
      return buildThingList(s, 3);
   }

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String delete() {
      return "x";
   }

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing deleteThing() {
      return new Thing("x");
   }

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Thing> deleteThingList() {
      return buildThingList("x", 3);
   }

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String head() {
      return "x";
   }

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   public String options() {
      return "x";
   }

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   public Thing optionsThing() {
      return new Thing("x");
   }

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Thing> optionsThingList() {
      return buildThingList("x", 3);
   }

   @GET
   @Path("exception/unhandled")
   public Thing exceptionUnhandled() throws Exception {
      throw new Exception("unhandled");
   }

   @GET
   @Path("exception/handled")
   public Thing exceptionHandled() throws Exception {
      throw new TestException("handled");
   }
   
   static List<Thing> buildThingList(String s, int listSize) {
      List<Thing> list = new ArrayList<Thing>();
      for (int i = 0; i < listSize; i++) {
         list.add(new Thing(s));
      }
      return list;
   }
}
