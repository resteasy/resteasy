package org.jboss.resteasy.links.test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;

@Path("/")
public interface BookStoreService {

   @Produces({"application/xml"})
   @Path("book/{id}")
   @GET
   Book getBookXML(@PathParam("id") String id);

   @Produces({"application/json"})
   @Path("book/{id}")
   @GET
   Book getBookJSON(@PathParam("id") String id);

   @Produces({"application/xml"})
   @Path("book/{id}/comments")
   @GET
   List<Comment> getBookCommentsXML(@PathParam("id") String id);

   @Produces({"application/json"})
   @Path("book/{id}/comments")
   @GET
   List<Comment> getBookCommentsJSON(@PathParam("id") String id);

   @Produces({"application/xml"})
   @GET
   @Path("book/{id}/comment-collection")
   ScrollableCollection getScrollableCommentsXML(@PathParam("id") String id, @MatrixParam("query") String query);

   @Produces({"application/json"})
   @GET
   @Path("book/{id}/comment-collection")
   ScrollableCollection getScrollableCommentsJSON(@PathParam("id") String id, @MatrixParam("query") String query);

}
