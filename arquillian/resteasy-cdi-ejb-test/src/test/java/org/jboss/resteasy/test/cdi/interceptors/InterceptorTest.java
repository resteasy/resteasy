package org.jboss.resteasy.test.cdi.interceptors;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.swing.text.Utilities;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.interceptors.Book;
import org.jboss.resteasy.cdi.interceptors.BookReader;
import org.jboss.resteasy.cdi.interceptors.BookReaderInterceptor;
import org.jboss.resteasy.cdi.interceptors.BookReaderInterceptorInterceptor;
import org.jboss.resteasy.cdi.interceptors.BookWriter;
import org.jboss.resteasy.cdi.interceptors.BookWriterInterceptor;
import org.jboss.resteasy.cdi.interceptors.BookWriterInterceptorInterceptor;
import org.jboss.resteasy.cdi.interceptors.ClassBinding;
import org.jboss.resteasy.cdi.interceptors.ClassInterceptorStereotype;
import org.jboss.resteasy.cdi.interceptors.ClassMethodInterceptorStereotype;
import org.jboss.resteasy.cdi.interceptors.FilterBinding;
import org.jboss.resteasy.cdi.interceptors.LifecycleBinding;
import org.jboss.resteasy.cdi.interceptors.PostConstructInterceptor;
import org.jboss.resteasy.cdi.interceptors.PreDestroyInterceptor;
import org.jboss.resteasy.cdi.interceptors.RequestFilterInterceptorBinding;
import org.jboss.resteasy.cdi.interceptors.ResponseFilterInterceptorBinding;
import org.jboss.resteasy.cdi.interceptors.Interceptor0;
import org.jboss.resteasy.cdi.interceptors.Interceptor1;
import org.jboss.resteasy.cdi.interceptors.Interceptor2;
import org.jboss.resteasy.cdi.interceptors.Interceptor3;
import org.jboss.resteasy.cdi.interceptors.InterceptorResource;
import org.jboss.resteasy.cdi.interceptors.JaxRsActivator;
import org.jboss.resteasy.cdi.interceptors.MethodBinding;
import org.jboss.resteasy.cdi.interceptors.ReaderInterceptorBinding;
import org.jboss.resteasy.cdi.interceptors.RequestFilterInterceptor;
import org.jboss.resteasy.cdi.interceptors.ResponseFilterInterceptor;
import org.jboss.resteasy.cdi.interceptors.Stereotyped;
import org.jboss.resteasy.cdi.interceptors.TestRequestFilter;
import org.jboss.resteasy.cdi.interceptors.TestResponseFilter;
import org.jboss.resteasy.cdi.interceptors.VisitList;
import org.jboss.resteasy.cdi.interceptors.WriterInterceptorBinding;
import org.jboss.resteasy.cdi.util.Constants;
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
public class InterceptorTest
{
   @Inject Logger log;

	@Deployment
	public static Archive<?> createTestArchive()
	{
	   WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
	         .addClasses(JaxRsActivator.class, Constants.class, UtilityProducer.class, Utilities.class, VisitList.class)
	         .addClasses(InterceptorResource.class, Interceptor0.class, Interceptor1.class)
	         .addClasses(ClassBinding.class, MethodBinding.class, Interceptor2.class, Interceptor3.class)
	         .addClasses(FilterBinding.class, RequestFilterInterceptorBinding.class, ResponseFilterInterceptorBinding.class)
	         .addClasses(RequestFilterInterceptor.class, ResponseFilterInterceptor.class, TestRequestFilter.class, TestResponseFilter.class)
	         .addClasses(ReaderInterceptorBinding.class, WriterInterceptorBinding.class)
	         .addClasses(Book.class, BookReader.class, BookWriter.class)
	         .addClasses(BookReaderInterceptor.class, BookWriterInterceptor.class)
	         .addClasses(BookReaderInterceptorInterceptor.class, BookWriterInterceptorInterceptor.class)
	         .addClasses(ClassInterceptorStereotype.class, ClassMethodInterceptorStereotype.class, Stereotyped.class)
	         .addClasses(LifecycleBinding.class, PostConstructInterceptor.class, PreDestroyInterceptor.class)
	         .addAsWebInfResource("interceptors/interceptorBeans.xml", "beans.xml");
	   System.out.println(war.toString(true));
	   return war;
	}
	   
	@Test
	public void testInterceptors() throws Exception
	{
	   log.info("starting testInterceptors()");
	   
      // Create book.
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/create/");
      Book book = new Book("RESTEasy: the Sequel");
      Type genericType = (new GenericType<Book>() {}).getGenericType();
      request.body(Constants.MEDIA_TYPE_TEST_XML_TYPE, book, genericType);
      ClientResponse<?> response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
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
