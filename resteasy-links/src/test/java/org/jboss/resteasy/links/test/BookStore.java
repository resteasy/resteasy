package org.jboss.resteasy.links.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

//import org.jboss.resteasy.links.AddJsonLinks;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.ParamBinding;
import org.jboss.resteasy.links.RESTServiceDiscovery;

@Path("/")
public class BookStore {

   private Map<String,Book> books = new HashMap<String,Book>();

   {
      Book book = new Book("foo", "bar");
      book.addComment(0, "great book");
      book.addComment(1, "terrible book");
      books.put(book.getTitle(), book);
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
//   @AddJsonLinks
   @LinkResource(value = Book.class, rel = "list")
   @GET
   @Path("books")
   public Collection<Book> getBooks(){
      return books.values();
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(value = Book.class, rel = "add")
   @POST
   @Path("books")
   public void addBook(Book book){
      books.put(book.getTitle(), book);
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
//   @AddJsonLinks
   @LinkResource(value = Book.class, pathParameters = "${title}")
   @GET
   @Path("book/{id}")
   public Book getBook(@PathParam("id") String id){
      return books.get(id);
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(value = Book.class, pathParameters = "${title}")
   @PUT
   @Path("book/{id}")
   public void updateBook(@PathParam("id") String id, Book book){
      books.put(id, book);
   }

   @LinkResource(value = Book.class, pathParameters = "${title}")
   @DELETE
   @Path("book/{id}")
   public void deleteBook(@PathParam("id") String id){
      books.remove(id);
   }

   // comments

   @Produces({"application/xml", "application/json"})
   @AddLinks
//   @AddJsonLinks
   @LinkResources({
      @LinkResource(value = Book.class, rel = "comments", pathParameters = "${title}"),
      @LinkResource(value = Comment.class, rel = "list", pathParameters = "${book.title}")
   })
   @GET
   @Path("book/{id}/comments")
   public Collection<Comment> getComments(@PathParam("id") String bookId){
      return books.get(bookId).getComments();
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
//   @AddJsonLinks
   @LinkResources({
      @LinkResource(value = Book.class, rel="comment-collection"),
      @LinkResource(value = Comment.class, rel="collection"),
      @LinkResource(value = ScrollableCollection.class, rel = "prev", constraint = "${this.start - this.limit >= 0}", queryParameters = {
            @ParamBinding(name = "start", value = "${this.start - this.limit}"),
            @ParamBinding(name = "limit", value = "${this.limit}") }),
      @LinkResource(value = ScrollableCollection.class, rel = "next", constraint = "${this.start + this.limit < this.totalRecords}", queryParameters = {
            @ParamBinding(name = "start", value = "${this.start + this.limit}"),
            @ParamBinding(name = "limit", value = "${this.limit}")
      }, matrixParameters = {@ParamBinding(name = "query", value = "${this.query}")})
   })
   @GET
   @Path("book/{id}/comment-collection")
   public ScrollableCollection getScrollableComments(@Context UriInfo uriInfo, @PathParam("id") String id, @QueryParam("start") int start, @QueryParam("limit") @DefaultValue("1") int limit, @MatrixParam("query") String query){
      List<Comment> comments = new ArrayList<Comment>();
      for (Comment comment : books.get(id).getComments()) {
         if (comment.getText().contains(query)) {
            comments.add(comment);
         }
      }
      start = start < 0 ? 0 : start;
      limit = limit < 1 ? 1 : limit;
      limit = (start + limit) > comments.size() ? comments.size() - start : limit;
      ScrollableCollection result = new ScrollableCollection(id, start, limit, comments.size(), comments.subList(start, start + limit), query);

      RESTServiceDiscovery discovery = new RESTServiceDiscovery();
      discovery.addLink(uriInfo.getBaseUriBuilder().build(), "home");
      result.setRest(discovery);

      return result;
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
//   @AddJsonLinks
   @LinkResource(value = Comment.class, rel = "self", pathParameters = {"${book.title}", "${id}"})
   @GET
   @Path("book/{id}/comment/{cid}")
   public Comment getComment(@PathParam("id") String bookId, @PathParam("cid") int commentId){
      return books.get(bookId).getComment(commentId);
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(value = Comment.class, rel = "add", pathParameters = {"${book.title}"})
   @POST
   @Path("book/{id}/comments")
   public void addComment(@PathParam("id") String bookId, Comment comment){
      books.get(bookId).getComments().add(comment);
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(value = Comment.class, rel = "update", pathParameters = {"${book.title}", "${id}"})
   @PUT
   @Path("book/{id}/comment/{cid}")
   public void updateComment(@PathParam("id") String bookId, @PathParam("cid") int commentId, Comment comment){
      books.get(bookId).getComment(commentId).setText(comment.getText());
   }

   @LinkResource(value = Comment.class, rel = "remove", pathParameters = {"${book.title}", "${id}"})
   @DELETE
   @Path("book/{id}/comment/{cid}")
   public void deleteComment(@PathParam("id") String bookId, @PathParam("cid") int commentId){
      Book book = books.get(bookId);
      Comment c = book.getComment(commentId);
      book.getComments().remove(c);
   }

}
