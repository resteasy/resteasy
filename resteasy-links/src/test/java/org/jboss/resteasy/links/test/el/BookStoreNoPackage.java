package org.jboss.resteasy.links.test.el;

//import org.jboss.resteasy.links.AddJsonLinks;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.test.Book;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class BookStoreNoPackage {

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
   @LinkResource(value = Book.class, pathParameters = "${title}")
   @GET
   @Path("book/{id}")
   public Book getBook(@PathParam("id") String id){
      return books.get(id);
   }
}
