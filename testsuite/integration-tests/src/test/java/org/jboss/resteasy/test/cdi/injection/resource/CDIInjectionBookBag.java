package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.Collection;
import java.util.HashSet;

import jakarta.ejb.Stateful;
import jakarta.enterprise.context.SessionScoped;

@Stateful
@SessionScoped
public class CDIInjectionBookBag implements CDIInjectionBookBagLocal {
    private HashSet<CDIInjectionBook> books = new HashSet<CDIInjectionBook>();

    public void addBook(CDIInjectionBook book) {
        books.add(book);
    }

    public Collection<CDIInjectionBook> getContents() {
        return new HashSet<CDIInjectionBook>(books);
    }

}
