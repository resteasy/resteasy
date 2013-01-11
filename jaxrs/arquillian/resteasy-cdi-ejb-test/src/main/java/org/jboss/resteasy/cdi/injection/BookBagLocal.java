package org.jboss.resteasy.cdi.injection;

import java.util.Collection;

import javax.ejb.Local;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 26, 2012
 */
@Local
public interface BookBagLocal
{
   public void addBook(Book book);
   public Collection<Book> getContents();
}

