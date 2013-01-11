package org.jboss.resteasy.test.cdi.decorators;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.decorators.Book;
import org.jboss.resteasy.cdi.decorators.BookReader;
import org.jboss.resteasy.cdi.decorators.BookReaderDecorator;
import org.jboss.resteasy.cdi.decorators.BookReaderInterceptor;
import org.jboss.resteasy.cdi.decorators.BookReaderInterceptorDecorator;
import org.jboss.resteasy.cdi.decorators.BookWriter;
import org.jboss.resteasy.cdi.decorators.BookWriterDecorator;
import org.jboss.resteasy.cdi.decorators.BookWriterInterceptor;
import org.jboss.resteasy.cdi.decorators.BookWriterInterceptorDecorator;
import org.jboss.resteasy.cdi.decorators.FilterBinding;
import org.jboss.resteasy.cdi.decorators.JaxRsActivator;
import org.jboss.resteasy.cdi.decorators.RequestFilterDecorator;
import org.jboss.resteasy.cdi.decorators.ResourceBinding;
import org.jboss.resteasy.cdi.decorators.ResourceDecorator;
import org.jboss.resteasy.cdi.decorators.ResourceInterceptor;
import org.jboss.resteasy.cdi.decorators.ResponseFilterDecorator;
import org.jboss.resteasy.cdi.decorators.TestRequestFilter;
import org.jboss.resteasy.cdi.decorators.TestResource;
import org.jboss.resteasy.cdi.decorators.TestResourceIntf;
import org.jboss.resteasy.cdi.decorators.TestResponseFilter;
import org.jboss.resteasy.cdi.decorators.VisitList;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Utilities;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@RunWith(Arquillian.class)
public class DecoratorsTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(JaxRsActivator.class, Constants.class, UtilityProducer.class, Utilities.class, VisitList.class)
            .addClasses(TestResourceIntf.class, TestResource.class, Book.class)
            .addClasses(BookReaderInterceptorDecorator.class, BookReaderInterceptor.class)
            .addClasses(BookReaderDecorator.class, BookReader.class)
            .addClasses(BookWriterInterceptorDecorator.class, BookWriterInterceptor.class)
            .addClasses(BookWriterDecorator.class, BookWriter.class)
            .addClasses(ResourceBinding.class, ResourceInterceptor.class, ResourceDecorator.class)
            .addClasses(FilterBinding.class, TestRequestFilter.class, RequestFilterDecorator.class, TestResponseFilter.class, ResponseFilterDecorator.class)
            .addAsWebInfResource("decorators/decoratorBeans.xml", "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testDecorators() throws Exception
   {
      log.info("starting testDecorators()");

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

      // Test order of decorator invocations.
      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/test/");
      response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.releaseConnection();
   }
}
