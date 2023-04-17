package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.Collection;

import jakarta.ejb.Local;

@Local
public interface CDIInjectionBookBagLocal {
    void addBook(CDIInjectionBook book);

    Collection<CDIInjectionBook> getContents();
}
