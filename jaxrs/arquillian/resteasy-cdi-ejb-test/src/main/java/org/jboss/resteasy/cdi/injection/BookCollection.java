package org.jboss.resteasy.cdi.injection;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Singleton
@ApplicationScoped
public class BookCollection
{  
   @ResourceBinding
   @PersistenceContext(unitName="test")
   EntityManager em;
   
   @Inject Logger log;
   
   public void addBook(Book book)
   {
      em.persist(book);
      log.info("persisted: " + book);
   }
   
   public Book getBook(int id)
   {
      return em.find(Book.class, id);
   }
   
   public Collection<Book> getBooks()
   {
      return em.createQuery("SELECT b FROM Book AS b", Book.class).getResultList();
   }
   
   public void empty()
   {
      em.createQuery("delete from Book").executeUpdate();
   }
}

