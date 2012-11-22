/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.test.ejb;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Counter;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.cdi.util.Utilities;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.ejb.Book;
import org.jboss.resteasy.ejb.EJBApplication;
import org.jboss.resteasy.ejb.EJBBookReader;
import org.jboss.resteasy.ejb.EJBBookReaderImpl;
import org.jboss.resteasy.ejb.EJBBookResource;
import org.jboss.resteasy.ejb.EJBBookWriterImpl;
import org.jboss.resteasy.ejb.EJBLocalResource;
import org.jboss.resteasy.ejb.EJBRemoteResource;
import org.jboss.resteasy.ejb.EJBResourceParent;
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
public class EJBTest
{
   @Inject private Logger log;
   @Inject EJBLocalResource localResource;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-ejb-test.war")
//      .addClasses(JaxRsActivator.class, 
      .addClass(EJBApplication.class)
      .addClasses(Book.class, Constants.class, Counter.class, UtilityProducer.class, Utilities.class)
      .addClasses(EJBBookReader.class, EJBBookReaderImpl.class)
      .addClasses(EJBBookWriterImpl.class)
      .addClasses(EJBResourceParent.class, EJBLocalResource.class, EJBRemoteResource.class, EJBBookResource.class)
      .setWebXML("ejbtest_web.xml")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      
      System.out.println(war.toString(true));
      return war;
   }
   
   /**
    * Verify that EJBBookReaderImpl, EJBBookWriterImpl, and EJBBookResource
    * are placed in the correct scope.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyScopesJaxRs() throws Exception
   {
      log.info("starting testVerifyScopesJaxRs()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/verifyScopes/");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      log.info("result: " + response.getEntity(Integer.class));
      assertEquals(200, response.getStatus());
      assertEquals(200, response.getEntity(Integer.class).intValue());
   }
   
   /**
    * Verify that EJBBookReaderImpl, EJBBookWriterImpl, and EJBBookResource
    * are placed in the correct scope.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyScopesLocalEJB() throws Exception
   {
      log.info("starting testVerifyScopesLocalEJB()");
      int result = localResource.verifyScopes();
      log.info("result: " + result);
      assertEquals(200, result);
   }
   
   /**
    * Verify that EJBBookReaderImpl, EJBBookWriterImpl, and EJBBookResource
    * are placed in the correct scope.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyScopesRemoteEJB() throws Exception
   {
      log.info("starting testVerifyScopesRemoteEJB()");
      
      // Get proxy to JAX-RS resource as EJB.
      EJBRemoteResource remoteResource = getRemoteResource();
      log.info("remote: " + remoteResource);
      int result = remoteResource.verifyScopes();
      log.info("result: " + result);
      assertEquals(200, result);
   }
   
   /**
    * Verify that EJBBookReader and EJBBookWriterImpl are correctly injected
    * into EJBBookResource.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyInjectionJaxRs() throws Exception
   {
      log.info("starting testVerifyInjectionJaxRs()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/verifyInjection/");
      ClientResponse<?> response = request.get();
      log.info("result: " + response.getEntity(Integer.class));
      assertEquals(200, response.getStatus());
      assertEquals(200, response.getEntity(Integer.class).intValue());
   }
   
   /**
    * Verify that EJBBookReader and EJBBookWriterImpl are correctly injected
    * into EJBBookResource.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyInjectionLocalEJB() throws Exception
   {
      log.info("starting testVerifyInjectionLocalEJB()");
      int result = localResource.verifyInjection();
      log.info("result: " + result);
      assertEquals(200, result);
   }
   
   /**
    * Verify that EJBBookReader and EJBBookWriterImpl are correctly injected
    * into EJBBookResource.
    * 
    * @throws Exception
    */
   @Test
   public void testVerifyInjectionRemoteEJB() throws Exception
   {
      log.info("starting testVerifyInjectionRemoteEJB()");
      
      // Get proxy to JAX-RS resource as EJB.
      EJBRemoteResource remoteResource = getRemoteResource();
      log.info("remote: " + remoteResource);
      int result = remoteResource.verifyInjection();
      log.info("result: " + result);
      assertEquals(200, result);
   }
   
   /**
    * Further addresses the use of EJBs as JAX-RS components.
    * 
    * @throws Exception
    */
   @Test
   public void testAsJaxRSResource() throws Exception
   {
      log.info("entering testAsJaxRSResource()");

      // Create book.
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/create/");
      Book book1 = new Book("RESTEasy: the Sequel");
      request.body(Constants.MEDIA_TYPE_TEST_XML, book1);
      ClientResponse<?> response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      int id1 = response.getEntity(int.class);
      log.info("id: " + id1);
      Assert.assertEquals(Counter.INITIAL_VALUE, id1);
      
      // Create another book.
      request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/create/");
      Book book2 = new Book("RESTEasy: It's Alive");
      request.body(Constants.MEDIA_TYPE_TEST_XML, book2);
      response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      int id2 = response.getEntity(int.class);
      log.info("id: " + id2);
      Assert.assertEquals(Counter.INITIAL_VALUE + 1, id2);
      
      // Retrieve first book.
      request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/book/" + id1);
      request.accept(Constants.MEDIA_TYPE_TEST_XML);
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      Book result = response.getEntity(Book.class);
      log.info("book: " + book1);
      Assert.assertEquals(book1, result);

      // Retrieve second book.
      request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/book/" + id2);
      request.accept(Constants.MEDIA_TYPE_TEST_XML);
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      result = response.getEntity(Book.class);
      log.info("book: " + book2);
      Assert.assertEquals(book2, result);
      
      // Verify that EJBBookReader and EJBBookWriter have been used, twice on each side.
      request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/uses/4");
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.releaseConnection();
      
      // Reset counter.
      request = new ClientRequest("http://localhost:8080/resteasy-ejb-test/reset");
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(204, response.getStatus());
      response.releaseConnection();
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
      int id1 = localResource.createBook(book1);
      log.info("id1: " + id1);
      Assert.assertEquals(Counter.INITIAL_VALUE, id1);
      
      // Create another book.
      Book book2 = new Book("RESTEasy: It's Alive");
      int id2 = localResource.createBook(book2);
      log.info("id2: " + id2);
      Assert.assertEquals(Counter.INITIAL_VALUE + 1, id2);
      
      // Retrieve first book.
      Book bookResponse1 = localResource.lookupBookById(id1);
      log.info("book1 response: " + bookResponse1);
      Assert.assertEquals(book1, bookResponse1);

      // Retrieve second book.
      Book bookResponse2 = localResource.lookupBookById(id2);
      log.info("book2 response: " + bookResponse2);
      Assert.assertEquals(book2, bookResponse2);
      
      // Verify that EJBBookReader and EJBBookWriter haven't been used.
      localResource.testUse(0);
      
      // Reset counter.
      localResource.reset();
   }
   
   /**
    * Invokes additional methods of JAX-RS resource as remote EJB.
    */
   @Test
   public void testAsRemoteEJB() throws Exception
   {
      log.info("entering testAsRemoteEJB()");

      // Get proxy to JAX-RS resource as EJB.
      EJBRemoteResource remoteResource = getRemoteResource();
      log.info("remote: " + remoteResource);

      // Create book.
      Book book1 = new Book("RESTEasy: the Sequel");
      int id1 = remoteResource.createBook(book1);
      log.info("id1: " + id1);
      Assert.assertEquals(Counter.INITIAL_VALUE, id1);
      
      // Create another book.
      Book book2 = new Book("RESTEasy: It's Alive");
      int id2 = remoteResource.createBook(book2);
      log.info("id2: " + id2);
      Assert.assertEquals(Counter.INITIAL_VALUE + 1, id2);
      
      // Retrieve first book.
      Book bookResponse1 = remoteResource.lookupBookById(id1);
      log.info("book1 response: " + bookResponse1);
      Assert.assertEquals(book1, bookResponse1);

      // Retrieve second book.
      Book bookResponse2 = remoteResource.lookupBookById(id2);
      log.info("book2 response: " + bookResponse2);
      Assert.assertEquals(book2, bookResponse2);
      
      // Verify that EJBBookReader and EJBBookWriter haven't been used.
      remoteResource.testUse(0);
      
      // Reset counter.
      remoteResource.reset();
   }
   
   private static EJBRemoteResource getRemoteResource() throws Exception
   {
      final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
      jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      final Context context = new InitialContext(jndiProperties);
      String name = "ejb:/resteasy-ejb-test/EJBBookResource!" + EJBRemoteResource.class.getName();
      return EJBRemoteResource.class.cast(context.lookup(name));
   }
}
