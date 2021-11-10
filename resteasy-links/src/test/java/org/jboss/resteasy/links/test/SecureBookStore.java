
package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class SecureBookStore {

   private Map<String,Book> books = new HashMap<String,Book>();

   {
      Book book = new Book("foo", "bar");
      book.addComment(Integer.toString(0), "great book");
      book.addComment(Integer.toString(1), "terrible book");
      books.put(book.getTitle(), book);
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
   @LinkResource(value = Book.class, constraint = "${s:hasPermission(this, 'read')}")
   @GET
   @Path("books")
   public Collection<Book> getBooks(){
      return books.values();
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(constraint = "${s:hasPermission(this, 'add')}")
   @POST
   @Path("books")
   public void addBook(Book book){
      books.put(book.getTitle(), book);
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
   @LinkResource(constraint = "${s:hasPermission(this, 'read')}")
   @GET
   @Path("book/{id}")
   public Book getBook(@PathParam("id") String id){
      return books.get(id);
   }

   @Consumes({"application/xml", "application/json"})
   @LinkResource(constraint = "${s:hasPermission(this, 'update')}")
   @PUT
   @Path("book/{id}")
   public void updateBook(@PathParam("id") String id, Book book){
      books.put(id, book);
   }

   @LinkResource(value = Book.class, constraint = "${s:hasPermission(this, 'delete')}")
   @DELETE
   @Path("book/{id}")
   public void deleteBook(@PathParam("id") String id){
      books.remove(id);
   }

}
