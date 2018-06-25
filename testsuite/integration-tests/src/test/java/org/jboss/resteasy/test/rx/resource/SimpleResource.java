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

@Path("")
public interface SimpleResource {

   @GET
   @Path("get/string")
   @Produces(MediaType.TEXT_PLAIN)
   String get();

   @GET
   @Path("get/thing")
   @Produces(MediaType.APPLICATION_JSON)
   Thing getThing();

   @GET
   @Path("get/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   List<Thing> getThingList();

   @PUT
   @Path("put/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   String put(String s);

   @PUT
   @Path("put/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   Thing putThing(String s);

   @PUT
   @Path("put/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   List<Thing> putThingList(String s);

   @POST
   @Path("post/string")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   String post(String s);

   @POST
   @Path("post/thing")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   Thing postThing(String s);

   @POST
   @Path("post/thing/list")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   List<Thing> postThingList(String s);

   @DELETE
   @Path("delete/string")
   @Produces(MediaType.TEXT_PLAIN)
   String delete();

   @DELETE
   @Path("delete/thing")
   @Produces(MediaType.APPLICATION_JSON)
   Thing deleteThing();

   @DELETE
   @Path("delete/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   List<Thing> deleteThingList();

   @HEAD
   @Path("head/string")
   @Produces(MediaType.TEXT_PLAIN)
   String head();

   @OPTIONS
   @Path("options/string")
   @Produces(MediaType.TEXT_PLAIN)
   String options();

   @OPTIONS
   @Path("options/thing")
   @Produces(MediaType.APPLICATION_JSON)
   Thing optionsThing();

   @OPTIONS
   @Path("options/thing/list")
   @Produces(MediaType.APPLICATION_JSON)
   List<Thing> optionsThingList();

   @GET
   @Path("exception/unhandled")
   Thing exceptionUnhandled() throws Exception;

   @GET
   @Path("exception/handled")
   Thing exceptionHandled() throws Exception;

}