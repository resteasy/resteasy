package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.logging.Logger;

@Singleton
@ApplicationScoped
public class CDIInjectionBookCollection {
    @PersistenceContext(unitName = "test")
    EntityManager em;

    @Inject
    Logger log;

    public void addBook(CDIInjectionBook book) {
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

