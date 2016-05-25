package org.jboss.resteasy.test.cdi.events;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.swing.text.Utilities;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.events.EventResource;
import org.jboss.resteasy.cdi.events.Read;
import org.jboss.resteasy.cdi.events.ReadIntercept;
import org.jboss.resteasy.cdi.events.Unused;
import org.jboss.resteasy.cdi.events.Write;
import org.jboss.resteasy.cdi.events.WriteIntercept;
import org.jboss.resteasy.cdi.events.Book;
import org.jboss.resteasy.cdi.events.BookReader;
import org.jboss.resteasy.cdi.events.BookReaderInterceptor;
import org.jboss.resteasy.cdi.events.BookWriter;
import org.jboss.resteasy.cdi.events.BookWriterInterceptor;
import org.jboss.resteasy.cdi.events.JaxRsActivator;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This is a collection of tests addressed to the interactions of 
 * Resteasy, CDI, EJB, and so forth in the context of a JEE Application Server.
 * 
 * It tests the injection of a variety of beans into Resteasy objects.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class EventsTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(JaxRsActivator.class, Constants.class, UtilityProducer.class, Utilities.class)
            .addClasses(Book.class, BookReader.class, BookWriter.class)
            .addClasses(BookReaderInterceptor.class, BookWriterInterceptor.class)
            .addClasses(EventResource.class)
            .addClasses(Process.class, Read.class, ReadIntercept.class)
            .addClasses(Unused.class, Write.class, WriteIntercept.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testEvents() throws Exception
   {
      log.info("starting testEvents()");

      // Create book.
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/create/");
      Book book = new Book("RESTEasy: the Sequel");
      Type genericType = (new GenericType<Book>() {}).getGenericType();
      request.body(Constants.MEDIA_TYPE_TEST_XML_TYPE, book, genericType);
      ClientResponse<?> response = request.post();
      assertEquals(200, response.getStatus());
      log.info("Status: " + response.getStatus());
      int id = response.getEntity(int.class);
      log.info("id: " + id);
      Assert.assertEquals(0, id);

      // Retrieve book.
      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/book/" + id);
      request.accept(Constants.MEDIA_TYPE_TEST_XML);
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      Book result = response.getEntity(Book.class);
      log.info("book: " + book);
      Assert.assertEquals(book, result);

      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/test/");
      response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.releaseConnection();
   }
}
