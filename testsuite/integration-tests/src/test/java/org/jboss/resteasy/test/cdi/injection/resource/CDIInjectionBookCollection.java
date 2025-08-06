package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.Collection;
import java.util.logging.Logger;

import jakarta.ejb.Singleton;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.test.cdi.util.CounterBinding;

@Singleton
@ApplicationScoped
public class CDIInjectionBookCollection {
    @PersistenceContext(unitName = "test")
    EntityManager em;

    @Inject
    Logger log;

    @Inject
    @CounterBinding
    private Counter counter; // application scoped singleton: injected as Weld proxy

    public void addBook(CDIInjectionBook book) {
        int id = counter.getNext();
        book.setId(id);

        em.persist(book);
        log.info("persisted: " + book);
    }

    public CDIInjectionBook getBook(int id) {
        return em.find(CDIInjectionBook.class, id);
    }

    public Collection<CDIInjectionBook> getBooks() {
        return em.createQuery("SELECT b FROM CDIInjectionBook AS b", CDIInjectionBook.class).getResultList();
    }

    public void empty() {
        em.createQuery("delete from CDIInjectionBook").executeUpdate();
    }
}
