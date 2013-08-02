package org.jboss.resteasy.test.cdi.injection;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.injection.Book;
import org.jboss.resteasy.cdi.injection.BookBag;
import org.jboss.resteasy.cdi.injection.BookBagLocal;
import org.jboss.resteasy.cdi.injection.BookCollection;
import org.jboss.resteasy.cdi.injection.BookMDB;
import org.jboss.resteasy.cdi.injection.BookReader;
import org.jboss.resteasy.cdi.injection.BookResource;
import org.jboss.resteasy.cdi.injection.BookWriter;
import org.jboss.resteasy.cdi.injection.DependentScoped;
import org.jboss.resteasy.cdi.injection.JaxRsActivator;
import org.jboss.resteasy.cdi.injection.NewBean;
import org.jboss.resteasy.cdi.injection.ResourceProducer;
import org.jboss.resteasy.cdi.injection.ScopeInheritingStereotype;
import org.jboss.resteasy.cdi.injection.ScopeStereotype;
import org.jboss.resteasy.cdi.injection.StatefulEJB;
import org.jboss.resteasy.cdi.injection.StereotypedApplicationScope;
import org.jboss.resteasy.cdi.injection.StereotypedDependentScope;
import org.jboss.resteasy.cdi.injection.UnscopedResource;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Counter;
import org.jboss.resteasy.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the use of MDBs with Resteasy, including the injection of a
 * JAX-RS resource into an MDB.
 *  
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 8, 2012
 */
@RunWith(Arquillian.class)
public class MDBInjectionTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(Book.class, BookResource.class, JaxRsActivator.class, Constants.class, UtilityProducer.class)
            .addClasses(Counter.class, BookCollection.class, BookReader.class, BookWriter.class)
            .addClasses(DependentScoped.class, StatefulEJB.class, UnscopedResource.class)
            .addClasses(BookBagLocal.class, BookBag.class)
            .addClasses(BookMDB.class)
            .addClasses(NewBean.class)
            .addClasses(ScopeStereotype.class, ScopeInheritingStereotype.class)
            .addClasses(StereotypedApplicationScope.class, StereotypedDependentScope.class)
            .addClasses(Resource.class, ResourceProducer.class, PersistenceUnitProducer.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .setManifest("reverseInjection/hornetq_manifest.mf")
            .addAsResource("injection/persistence.xml", "META-INF/persistence.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Before
   public void preparePersistenceTest() throws Exception
   {
      System.out.println("Dumping old records...");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/empty/");
      ClientResponse<?> response = request.post();
      response.releaseConnection();
   }
   
   /**
    * Tests the injection of JMS Producers, Consumers, Queues, and MDBs using producer fields and methods.
    */
   @Test
   public void testMDB() throws Exception
   {
      log.info("starting testJMS()");
      
      // Send a book title.
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/produceMessage/");
      String title = "Dead Man Lounging";
      Book book = new Book(23, title);
      request.body(Constants.MEDIA_TYPE_TEST_XML, book);
      ClientResponse<?> response = request.post();
      log.info("status: " + response.getStatus());
      log.info(response.getEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      
      // Verify that the received book title is the one that was sent.
      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/mdb/consumeMessage/");     
      response = request.get();
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(title, response.getEntity(String.class));
   }
}
