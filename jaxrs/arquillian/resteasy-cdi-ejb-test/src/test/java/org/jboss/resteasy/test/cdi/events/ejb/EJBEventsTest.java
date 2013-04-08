package org.jboss.resteasy.test.cdi.events.ejb;

import java.util.logging.Logger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.events.ejb.Book;
import org.jboss.resteasy.cdi.events.ejb.EventObserver;
import org.jboss.resteasy.cdi.events.ejb.EventObserverImpl;
import org.jboss.resteasy.cdi.events.ejb.EventSource;
import org.jboss.resteasy.cdi.events.ejb.EventSourceImpl;
import org.jboss.resteasy.cdi.events.ejb.ProcessRead;
import org.jboss.resteasy.cdi.events.ejb.ProcessReadWrite;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 29, 2012
 */
@RunWith(Arquillian.class)
public class EJBEventsTest
{
   @Inject private Logger log;
   @Inject EventSource eventSource;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-ejb-test.war")
      .addClasses(UtilityProducer.class, Book.class)
      .addClasses(EventObserver.class, EventObserverImpl.class)
      .addClasses(EventSource.class, EventSourceImpl.class)
      .addClasses(Process.class, ProcessRead.class, ProcessReadWrite.class)
      .setWebXML("ejb/ejbtest_web.xml")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }
   
   /**
    * Invokes additional methods of JAX-RS resource as local EJB.
    */
   @Test
   public void testAsLocalEJB() throws Exception
   {
      log.info("entering testAsLocalEJB()");

      // Create book.
      Book book1 = new Book("RESTEasy: the Sequel");
      int id1 = eventSource.createBook(book1);
      log.info("id1: " + id1);
      Assert.assertEquals(0, id1);
      
      // Create another book.
      Book book2 = new Book("RESTEasy: It's Alive");
      int id2 = eventSource.createBook(book2);
      log.info("id2: " + id2);
      Assert.assertEquals(1, id2);
      
      // Retrieve first book.
      Book bookResponse1 = eventSource.lookupBookById(id1);
      log.info("book1 response: " + bookResponse1);
      Assert.assertEquals(book1, bookResponse1);

      // Retrieve second book.
      Book bookResponse2 = eventSource.lookupBookById(id2);
      log.info("book2 response: " + bookResponse2);
      Assert.assertEquals(book2, bookResponse2);
   }
}
