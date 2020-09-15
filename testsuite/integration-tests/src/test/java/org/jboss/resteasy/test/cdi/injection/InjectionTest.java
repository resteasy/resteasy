package org.jboss.resteasy.test.cdi.injection;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketPermission;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBook;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookBag;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookBagLocal;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookCollection;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookReader;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookResource;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookWriter;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionDependentScoped;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionNewBean;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceProducer;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionScopeInheritingStereotype;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionScopeStereotype;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStatefulEJB;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedApplicationScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedDependentScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionUnscopedResource;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.test.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails This is a collection of tests addressed to the interactions of
 *                    Resteasy, CDI, EJB, and so forth in the context of a JEE Application Server.
 *                    It tests the injection of a variety of beans into Resteasy objects.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({
    ExpectedFailingWithStandaloneMicroprofileConfiguration.class, // MP is missing jboss.naming.context.java.jboss.exported.jms.RemoteConnectionFactory
    NotForBootableJar.class // needs ejb + standalone-full when creating the bootable and inherit all default packages, this creates a 250MB bootable JAR which is useless
})
public class InjectionTest extends AbstractInjectionTestBase {
   protected static final Logger log = LogManager.getLogger(InjectionTest.class.getName());

   static Client client;

   static ParameterizedType BookCollectionType = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{CDIInjectionBook.class};
      }

      @Override
      public Type getRawType() {
         return Collection.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public class InjectionCollection implements ParameterizedType {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{CDIInjectionBook.class};
      }

      @Override
      public Type getRawType() {
         return Collection.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   }

   private static int invocationCounter;

   @SuppressWarnings(value = "unchecked")
   @Deployment
   public static Archive<?> createTestArchive() throws Exception {
      initQueue();
      WebArchive war = TestUtil.prepareArchive(InjectionTest.class.getSimpleName());
      war.addClass(AbstractInjectionTestBase.class)
            .addClasses(CDIInjectionBook.class, CDIInjectionBookResource.class, Constants.class, UtilityProducer.class, BookCollectionType.getClass())
            .addClasses(Counter.class, CDIInjectionBookCollection.class, CDIInjectionBookReader.class, CDIInjectionBookWriter.class)
            .addClasses(CDIInjectionDependentScoped.class, CDIInjectionStatefulEJB.class, CDIInjectionUnscopedResource.class)
            .addClasses(CDIInjectionBookBagLocal.class, CDIInjectionBookBag.class)
            .addClasses(CDIInjectionNewBean.class)
            .addClasses(CDIInjectionScopeStereotype.class, CDIInjectionScopeInheritingStereotype.class)
            .addClasses(CDIInjectionStereotypedApplicationScope.class, CDIInjectionStereotypedDependentScope.class)
            .addClasses(Resource.class, CDIInjectionResourceProducer.class, PersistenceUnitProducer.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource(InjectionTest.class.getPackage(), "persistence.xml", "META-INF/persistence.xml");

      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new HibernateValidatorPermission("accessPrivateMembers"),
            new SocketPermission(PortProviderUtil.getHost(), "resolve")),
         "permissions.xml");

      return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, InjectionTest.class.getSimpleName());
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
      client.close();
   }

   private ResteasyProviderFactory factory;
   @Before
   public void preparePersistenceTest() throws Exception {
      log.info("Dumping old records.");

      WebTarget base = client.target(generateURL("/empty/"));
      Response response = base.request().post(Entity.text(new String()));
      invocationCounter++;
      response.close();

      // Create an instance and set it as the singleton to use
      factory = ResteasyProviderFactory.newInstance();
      ResteasyProviderFactory.setInstance(factory);
      RegisterBuiltin.register(factory);
   }

   @After
   public void cleanup() {
      // Clear the singleton
      ResteasyProviderFactory.clearInstanceIfEqual(factory);
   }

   /**
    * @tpTestDetails Addresses the correct handling of built-in scopes. E.g.
    * 1) Providers are in the application scope, whether they are annotated or not.
    * 2) Resources are in the request scope, annotated or not.
    * 3) Objects in the dependent scope, when injected into JAX-RS objects, are handled properly.
    * 4) Singletons in the application scope, when injected in request scoped JAX-RS resources as
    *    EJB proxies or Weld proxies, are handled properly.
    * A side effect of 3) and 4) is to test that beans managed by CDI (managed beans, singleton beans,
    * stateless EJBs) are injected properly into JAX-RS objects.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testVerifyScopes() throws Exception {
      log.info("starting testVerifyScopes()");

      WebTarget base = client.target(generateURL("/verifyScopes/"));
      Response response = base.request().get();
      invocationCounter++;
      log.info("First status: " + response.getStatus());
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
      response = base.request().get();
      invocationCounter++;
      log.info("Second status: " + response.getStatus());
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails Addresses the injection of managed beans, singletons, and stateless EJBs into JAX-RS objects.
    *                Uses a singleton (BookCollection) to interact with an EntityManager.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testEJBs() throws Exception {
      log.info("starting testEJBs()");

      // Create book.
      WebTarget base = client.target(generateURL("/create/"));
      CDIInjectionBook book1 = new CDIInjectionBook("RESTEasy: the Sequel");
      Response response = base.request().post(Entity.entity(book1, "application/test+xml"));
      invocationCounter++;
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      log.info("Status: " + response.getStatus());
      int id1 = response.readEntity(int.class);
      log.info("Id of response book: " + id1);
      Assert.assertEquals(Counter.INITIAL_VALUE, id1);
      response.close();

      // Create another book.
      base = client.target(generateURL("/create/"));
      CDIInjectionBook book2 = new CDIInjectionBook("RESTEasy: It's Alive");
      response = base.request().post(Entity.entity(book2, "application/test+xml"));
      invocationCounter++;
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      log.info("Status: " + response.getStatus());
      int id2 = response.readEntity(int.class);
      log.info("Id of response book: " + id2);
      Assert.assertEquals(Counter.INITIAL_VALUE + 1, id2);
      response.close();

      // Retrieve first book.
      base = client.target(generateURL("/book/" + id1));
      response = base.request().accept("application/test+xml").get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      CDIInjectionBook result = response.readEntity(CDIInjectionBook.class);
      log.info("Book from response: " + book1);
      Assert.assertEquals(book1, result);
      response.close();

      // Retrieve second book.
      base = client.target(generateURL("/book/" + id2));
      response = base.request().accept("application/test+xml").get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      result = response.readEntity(CDIInjectionBook.class);
      log.info("Book from response: " + book2);
      Assert.assertEquals(book2, result);
      response.close();

      // Retrieve all books.
      base = client.target(generateURL("/books"));
      response = base.request().accept(MediaType.APPLICATION_XML).get();
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      @SuppressWarnings("unchecked")
      Collection<CDIInjectionBook> books = response.readEntity(new GenericType<>(BookCollectionType));
      log.info("Collection from response: " + books);
      Assert.assertEquals(2, books.size());
      Iterator<CDIInjectionBook> it = books.iterator();
      CDIInjectionBook b1 = it.next();
      CDIInjectionBook b2 = it.next();
      log.info("First book in list: " + b1);
      log.info("Second book in list: " + b2);
      Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));
      response.close();

      // Test EntityManager injected in BookResource
      base = client.target(generateURL("/entityManager"));
      response = base.request().post(Entity.text(new String()));
      invocationCounter++;
      log.info("Status: " + response.getStatus());
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails This test verifies that a session scoped SFSB survives throughout the course of a session and is
    * re-injected into the request scoped BookResource over the course of the session.  Also, it is destroyed
    * and replaced when an invocation is made on BookResource after the session ends.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSessionScope() throws Exception {
      log.info("starting testSessionScope()");
      client.close();
      client = ClientBuilder.newClient();

      // Need to supply each ClientRequest with a single ClientExecutor to maintain a single
      // cookie cache, which keeps the session alive.
      //ClientExecutor executor = new ApacheHttpClient4Executor();

      // Create a book, which gets stored in the session scoped BookBag.
      WebTarget base = client.target(generateURL("/session/add/"));
      CDIInjectionBook book1 = new CDIInjectionBook(13, "Dead Man Napping");
      Response response = base.request().post(Entity.entity(book1, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();

      // Create another book, which should get stored in the same BookBag.
      base = client.target(generateURL("/session/add/"));
      CDIInjectionBook book2 = new CDIInjectionBook(Counter.INITIAL_VALUE, "Dead Man Dozing");
      response = base.request().post(Entity.entity(book2, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();

      // Get the current contents of the BookBag, and verify that it holds both of the books sent in the
      // previous two invocations.  When this method is called, the session is terminated.
      base = client.target(generateURL("/session/get/"));
      response = base.request().get();
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      @SuppressWarnings("unchecked")
      Collection<CDIInjectionBook> books = response.readEntity(new GenericType<>(BookCollectionType));
      log.info("Collection from response: " + books);
      Assert.assertEquals(2, books.size());
      Iterator<CDIInjectionBook> it = books.iterator();
      CDIInjectionBook b1 = it.next();
      CDIInjectionBook b2 = it.next();
      log.info("First book in list: " + b1);
      log.info("Second book in list: " + b2);
      Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));
      response.close();

      // Verify that the BookBag has been replaced by a new, empty one for the new session.
      base = client.target(generateURL("/session/test/"));
      response = base.request().post(Entity.text(new String()));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails Tests the injection of JMS Producers, Consumers, and Queues using producer fields and methods.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJMS() throws Exception {
      log.info("starting testJMS()");

      // Send a book title.
      WebTarget base = client.target(generateURL("/produceMessage/"));
      String title = "Dead Man Lounging";
      CDIInjectionBook book = new CDIInjectionBook(23, title);
      Response response = base.request().post(Entity.entity(book, Constants.MEDIA_TYPE_TEST_XML));
      invocationCounter++;
      log.info("status: " + response.getStatus());
      log.info(response.readEntity(String.class));
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();

      // Verify that the received book title is the one that was sent.
      base = client.target(generateURL("/queue/consumeMessage/"));
      log.info("consuming book");
      response = base.request().get();
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(title, response.readEntity(String.class));
      response.close();
   }

   /**
    * @tpTestDetails Verifies that BookResource.postConstruct() and preDestroy() are called for each invocation.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testPostConstructPreDestroy() throws Exception {
      log.info("starting testPostConstructPreDestroy()");

      // Send a book title.
      log.info("invocationCounter: " + invocationCounter);
      WebTarget base = client.target(generateURL("/getCounters/"));
      Response response = base.request().get();
      log.info("status: " + response.getStatus());
      String result = response.readEntity(String.class);
      log.info("Response: " + result);
      log.info("InvocationCounter: " + invocationCounter);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      String[] counters = result.split(":");
      Assert.assertTrue(invocationCounter + 1 == Integer.valueOf(counters[0])); // invocations of postConstruct()
      Assert.assertTrue(invocationCounter == Integer.valueOf(counters[1]));     // invocations of preDestroy()
      response.close();
   }

   /**
    * @tpTestDetails Verifies that ResourceProducer disposer method has been called for Queue.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testDisposer() throws Exception {
      log.info("starting testDisposer()");
      WebTarget base = client.target(generateURL("/disposer/"));
      Response response = base.request().get();
      invocationCounter++;
      log.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }
}
