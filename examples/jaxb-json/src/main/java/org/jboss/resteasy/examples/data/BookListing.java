package org.jboss.resteasy.examples.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name="listing")
public class BookListing
{
   private List<Book> books;

   public BookListing()
   {
   }

   public BookListing(List<Book> books)
   {
      this.books = books;
   }

   @XmlElement(name="books")
   public List<Book> getBooks()
   {
      return books;
   }
}
