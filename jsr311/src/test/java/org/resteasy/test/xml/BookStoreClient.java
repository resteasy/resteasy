package org.resteasy.test.xml;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
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
   @ProduceMime("text/xml")
   Book getBookByISBN(@PathParam("isbn")String isbn);

   @PUT
   @Path("books")
   @ConsumeMime("text/xml")
   void addBook(Book book);

   @GET
   @Path("books")
   @ProduceMime("text/xml")
   Collection<Book> getAllBooks();
}
