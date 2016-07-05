package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface CDIInjectionBookBagLocal {
    void addBook(CDIInjectionBook book);

    Collection<CDIInjectionBook> getContents();
}

