package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import jakarta.ws.rs.PathParam;

@Local
public interface EJBEventsSource {
   int createBook(EJBBook book);

   EJBBook lookupBookById(@PathParam("id") int id);

   boolean test();
}
