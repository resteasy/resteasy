package org.jboss.resteasy.test.xml;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("bookstore")
public interface BookStoreClient
{
   @GET
   @Path("books/{isbn}")
   @Produces("text/xml")
   Book getBookByISBN(@PathParam("isbn") String isbn);

   @PUT
   @Path("books")
   @Consumes("text/xml")
   void addBook(Book book);

   @GET
   @Path("books")
   @Produces("text/xml")
   Collection<Book> getAllBooks();

   @GET
   @Path("books/{isbn}")
   @Produces("application/json")
   Book getBookByISBNJson(@PathParam("isbn") String isbn);

   @PUT
   @Path("books")
   @Consumes("application/json")
   void addBookJson(Book book);

   @GET
   @Path("books")
   @Produces("application/json")
   Collection<Book> getAllBooksJson();
}
