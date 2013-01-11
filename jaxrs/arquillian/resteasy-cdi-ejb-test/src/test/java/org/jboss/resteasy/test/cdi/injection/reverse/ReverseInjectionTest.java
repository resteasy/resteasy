package org.jboss.resteasy.test.cdi.injection.reverse;

import static org.jboss.resteasy.cdi.injection.reverse.ReverseInjectionResource.NON_CONTEXTUAL;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

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
import org.jboss.resteasy.cdi.injection.ResourceBinding;
import org.jboss.resteasy.cdi.injection.ResourceProducer;
import org.jboss.resteasy.cdi.injection.StatefulEJB;
import org.jboss.resteasy.cdi.injection.StereotypedApplicationScope;
import org.jboss.resteasy.cdi.injection.StereotypedDependentScope;
import org.jboss.resteasy.cdi.injection.UnscopedResource;
import org.jboss.resteasy.cdi.injection.reverse.EJBHolder;
import org.jboss.resteasy.cdi.injection.reverse.EJBHolderLocal;
import org.jboss.resteasy.cdi.injection.reverse.EJBHolderRemote;
import org.jboss.resteasy.cdi.injection.reverse.EJBInterface;
import org.jboss.resteasy.cdi.injection.reverse.ReverseInjectionResource;
import org.jboss.resteasy.cdi.injection.reverse.StatefulApplicationScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.cdi.injection.reverse.StatefulApplicationScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.cdi.injection.reverse.StatefulDependentScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.cdi.injection.reverse.StatefulDependentScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.cdi.injection.reverse.StatefulRequestScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.cdi.injection.reverse.StatefulRequestScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.cdi.injection.reverse.StatelessEJBwithJaxRsComponents;
import org.jboss.resteasy.cdi.injection.reverse.StatelessEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.cdi.util.Counter;
import org.jboss.resteasy.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.cdi.util.Utilities;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ReverseInjectionTest goes beyond InjectionTest by injecting Resteasy objects
 * into other kinds of beans, e.g., EJBs.
 * 
 * For example, 
 * 
 * *) an EJB called EJBHolder is injected into the Resteasy resource ReverseInjectionResource
 * *) a variety of EJBs, e.g., StatelessEJBwithJaxRsComponents, are injected  into EJBHolder
 * *) a variety of Resteasy resources are injected into StatelessEJBwithJaxRsComponents and similar EJBs.
 * 
 * Also, the EJBs like StatelessEJBwithJaxRsComponents are injected into EJBHolder using both 
 * @EJB and @Inject, and the semantics of both are tested.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class ReverseInjectionTest
{
   @Inject Logger log;

   static ParameterizedType BookCollectionType = new ParameterizedType()
   {
      @Override
      public Type[] getActualTypeArguments()
      {
         return new Type[]{Book.class};
      }
      @Override
      public Type getRawType()
      {
         return Collection.class;
      }
      @Override
      public Type getOwnerType()
      {
         return null;
      }
   };

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-reverse-injection-test.war")
      .addClasses(Constants.class, PersistenceUnitProducer.class, UtilityProducer.class, Utilities.class)
      .addClasses(JaxRsActivator.class, Book.class, BookResource.class)
      .addClasses(ResourceBinding.class, ResourceProducer.class)
      .addClasses(Counter.class, BookCollection.class, BookReader.class, BookWriter.class)
      .addClasses(DependentScoped.class, StatefulEJB.class, UnscopedResource.class)
      .addClasses(BookBagLocal.class, BookBag.class)
      .addClasses(BookMDB.class)
      .addClasses(EJBInterface.class)
      .addClasses(StatelessEJBwithJaxRsComponentsInterface.class, StatelessEJBwithJaxRsComponents.class)
      .addClasses(StatefulDependentScopedEJBwithJaxRsComponentsInterface.class, StatefulDependentScopedEJBwithJaxRsComponents.class)
      .addClasses(StatefulRequestScopedEJBwithJaxRsComponentsInterface.class, StatefulRequestScopedEJBwithJaxRsComponents.class)
      .addClasses(StatefulApplicationScopedEJBwithJaxRsComponentsInterface.class, StatefulApplicationScopedEJBwithJaxRsComponents.class)
      .addClasses(EJBHolderRemote.class, EJBHolderLocal.class, EJBHolder.class)
      .addClasses(ReverseInjectionResource.class)
      .addClasses(NewBean.class, StereotypedApplicationScope.class, StereotypedDependentScope.class)
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .setManifest("reverseInjection/hornetq_manifest.mf")
      .addAsResource("injection/persistence.xml", "META-INF/persistence.xml");
      System.out.println(war.toString(true));
      return war;
   }

   /**
    *  Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
    *  a stateless EJB3.  The target SLSB is not a contextual object, since it is
    *  obtained through JNDI, so CDI performs injections when the SLSB is created,
    *  but there is no scope management.  It follows that the target SLSB is not recreated
    *  for the second invocation.
    */
   @Test
   public void testSLSB() throws Exception
   {
      log.info("starting testSLSB()");
      
      final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
      jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      final Context context = new InitialContext(jndiProperties);
      String name = "ejb:/resteasy-reverse-injection-test/StatelessEJBwithJaxRsComponents!" + StatelessEJBwithJaxRsComponentsInterface.class.getName();
      StatelessEJBwithJaxRsComponentsInterface remote = StatelessEJBwithJaxRsComponentsInterface.class.cast(context.lookup(name));
      log.info("remote: " + remote);
      remote.setUp(NON_CONTEXTUAL);
      Assert.assertTrue(remote.test(NON_CONTEXTUAL));
   }
   
   /**
    *  Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
    *  a @Dependent annotated stateful EJB3.  The target SFSB is not a contextual object,
    *  since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
    *  but there is no scope management.  It follows that the target SFSB is not recreated
    *  for the second invocation.
    */
   @Test
   public void testSFSBDependentScope() throws Exception
   {
      log.info("starting testSFSBDependentScope()");
      doTestSFSB("Dependent");
   }
   
   /**
    *  Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
    *  a @RequestScoped annotated stateful EJB3.  The target SFSB is not a contextual object,
    *  since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
    *  but there is no scope management.  It follows that the target SFSB is not recreated
    *  for the second invocation.
    */
   @Test
   public void testSFSBRequestScope() throws Exception
   {
      log.info("starting testSFSBRequestScope()");
      doTestSFSB("Request");

   }
   
   /**
    *  Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
    *  a @ApplicationScoped annotated stateful EJB3.  The target SFSB is not a contextual object,
    *  since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
    *  but there is no scope management.  It follows that the target SFSB is not recreated
    *  for the second invocation.
    */
   @Test
   public void testSFSBApplicationScope() throws Exception
   {
      log.info("starting testSFSBApplicationScope()");
      doTestSFSB("Application");

   }
   
   private void doTestSFSB(String scope) throws Exception
   {
      final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
      jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      final Context context = new InitialContext(jndiProperties);
      String className = "Stateful" + scope + "ScopedEJBwithJaxRsComponents";
      Class<?> viewName = Class.forName("org.jboss.resteasy.cdi.injection.reverse." + className + "Interface");
      String lookup = "ejb:/resteasy-reverse-injection-test/" + className + "!" + viewName.getName() + "?stateful";
      log.info("lookup: " + lookup);
      EJBInterface remote = EJBInterface.class.cast(context.lookup(lookup));
      log.info("remote: " + remote);
      remote.setUp(NON_CONTEXTUAL);
      Assert.assertTrue(remote.test(NON_CONTEXTUAL));
   }

   /**
    * Verifies the scopes of the EJBs used in this set of tests.
    */
   @Test
   public void testEJBHolderInResourceScopes() throws Exception
   {
      log.info("starting testEJBHolderInResourceScopes()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-reverse-injection-test/rest/reverse/testScopes/");
      ClientResponse<?> response = request.post();
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();
   }
   
   @Test
   public void testEJBHolderInResource() throws Exception
   {
      log.info("starting testEJBHolderInResource()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-reverse-injection-test/rest/reverse/setup/");
      ClientResponse<?> response = request.post();
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();
      request = new ClientRequest("http://localhost:8080/resteasy-reverse-injection-test/rest/reverse/test/");
      response = request.post();
      log.info("status: " + response.getStatus());
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();
   }
   
   /**
    * Tests the injection of a JAX-RS resource (BookResource) into an MDB.
    */
   @Test
   public void testMDB() throws Exception
   {
      log.info("starting testMDB()");
      String destinationName = "queue/test";
      Context ic = null;
      ConnectionFactory cf = null;
      Connection connection = null;
      try
      {
         ic = new InitialContext();
         cf = (ConnectionFactory)ic.lookup("/ConnectionFactory");
         Queue queue = (Queue)ic.lookup(destinationName);
         connection = cf.createConnection();
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer producer = session.createProducer(queue);
         connection.start();
         Book book1 = new Book("Dead Man Snoring");
         TextMessage message = session.createTextMessage(book1.getName());
         producer.send(message);
         log.info("Message sent to to the JMS Provider: " + book1.getName());
         Book book2 = new Book("Dead Man Drooling");
         message = session.createTextMessage(book2.getName());
         producer.send(message);
         log.info("Message sent to to the JMS Provider: " + book2.getName());
         ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-reverse-injection-test/rest/mdb/books");
         ClientResponse<?> response = request.get();
         log.info("status: " + response.getStatus());
         Assert.assertEquals(200, response.getStatus());
         @SuppressWarnings("unchecked")
         Collection<Book> books = response.getEntity(Collection.class, BookCollectionType);
         log.info("Collection: " + books);
         Assert.assertEquals(2, books.size());
         Iterator<Book> it = books.iterator();
         Book b1 = it.next();
         Book b2 = it.next();
         Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
      finally
      {
         if (connection != null)
         {
            try
            {
               connection.close();
            } catch (JMSException e)
            {
               e.printStackTrace();
            }
         }
      }
   }
}
