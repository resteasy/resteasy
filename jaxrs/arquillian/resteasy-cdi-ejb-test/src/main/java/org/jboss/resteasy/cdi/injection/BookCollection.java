/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
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

