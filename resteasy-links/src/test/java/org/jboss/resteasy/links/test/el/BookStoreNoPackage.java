package org.jboss.resteasy.links.test.el;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.test.Book;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class BookStoreNoPackage {

   private Map<String,Book> books = new HashMap<String,Book>();

   {
      Book book = new Book("foo", "bar");
      book.addComment(Integer.toString(0), "great book");
      book.addComment(Integer.toString(1), "terrible book");
      books.put(book.getTitle(), book);
   }

   @Produces({"application/xml", "application/json"})
   @AddLinks
   @LinkResource(value = Book.class, pathParameters = "${title}")
   @GET
   @Path("book/{id}")
   public Book getBook(@PathParam("id") String id){
      return books.get(id);
   }
}
