package org.resteasy.test.xml;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.UriParam;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("bookstore")
public class BookStore {
    private static Map<String, Book> availableBooks = new HashMap<String, Book>();

    static {
        availableBooks.put("596529260", new Book("Leonard Richardson", "596529260", "RESTful Web Services"));
        availableBooks.put("3897217279", new Book("Sam Ruby", "3897217279", "Web Services mit REST"));
    }

    @GET
    @Path("books/{isbn}")
    @ProduceMime("text/xml")
    public Book getBookByISBN(@UriParam("isbn")String isbn) {
        return availableBooks.get(isbn);
    }

    @PUT
    @Path("books")
    @ConsumeMime("text/xml")
    public void addBook(Book book) {
        availableBooks.put(book.getISBN(), book);
    }

    @GET
    @Path("books")
    @ProduceMime("text/xml")
    public Collection<Book> getAllBooks() {
        return availableBooks.values();
    }
}
