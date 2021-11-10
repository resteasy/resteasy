package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ejb.Local;
import java.util.Collection;

@Local
public interface CDIInjectionBookBagLocal {
   void addBook(CDIInjectionBook book);

   Collection<CDIInjectionBook> getContents();
}
