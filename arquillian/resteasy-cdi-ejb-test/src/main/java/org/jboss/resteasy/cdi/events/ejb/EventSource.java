package org.jboss.resteasy.cdi.events.ejb;

import javax.ejb.Local;
import javax.ws.rs.PathParam;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 29, 2012
 */
@Local
public interface EventSource
{
   public int createBook(Book book);
   public Book lookupBookById(@PathParam("id") int id);
   public boolean test();
}

