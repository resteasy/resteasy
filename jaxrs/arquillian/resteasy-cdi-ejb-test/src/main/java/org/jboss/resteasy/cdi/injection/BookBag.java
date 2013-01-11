package org.jboss.resteasy.cdi.injection;

import java.util.Collection;
import java.util.HashSet;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 26, 2012
 */
@Stateful
@SessionScoped
public class BookBag implements BookBagLocal
{
   private HashSet<Book> books = new HashSet<Book>();
   
   public void addBook(Book book)
   {
      books.add(book);
   }

   public Collection<Book> getContents()
   {
      return new HashSet<Book>(books);
   }

}

