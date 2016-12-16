package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("/bookstore")
public class BookStoreResource {
   private static Map<String, Book> availableBooks = new HashMap<>();

   static {
      availableBooks.put("596529260", new Book("Leonard Richardson", "596529260", "RESTful Web Services"));
      availableBooks.put("3897217279", new Book("Sam Ruby", "3897217279", "Web Services mit REST"));
   }

   @GET
   @Path("/books/{isbn}")
   @Produces({"text/xml", "application/json"})
   public Book getBookByISBN(@PathParam("isbn") String isbn) {
      return availableBooks.get(isbn);
   }

   @PUT
   @Path("/books")
   @Consumes({"text/xml", "application/json"})
   public void addBook(Book book) {
      availableBooks.put(book.getIsbn(), book);
   }

   @GET
   @Path("/books")
   @Produces({"text/xml", "application/json"})
   public Collection<Book> getAllBooks() {
      return availableBooks.values();
   }


}
