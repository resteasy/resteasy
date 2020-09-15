package org.jboss.resteasy.test.response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteApplicationOne;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteApplicationTwo;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteResourceOne;
import org.jboss.resteasy.test.response.resource.DuplicitePathDupliciteResourceTwo;
import org.jboss.resteasy.test.response.resource.DuplicitePathMethodResource;
import org.jboss.resteasy.test.response.resource.DuplicitePathNoDupliciteApplication;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-3459
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no log check support for bootable-jar in RESTEasy TS so far
public class DuplicitePathTest {
   static ResteasyClient client;

   /**
    * Init servlet warning count ( WFLYUT0101: Duplicate servlet mapping /a/* found )
    */
   private static int initServletWarningsCount;

   /**
    * Get RESTEasy warning count
    */
   private static int getWarningCount() {
      return TestUtil.getWarningCount("RESTEASY002142", false, DEFAULT_CONTAINER_QUALIFIER);
   }

   /**
    * Gets servlet warning count
    * Warning comes from server (outside of the resteasy)
    * Example: WFLYUT0101: Duplicate servlet mapping /a/* found
    */
   private static int getServletMappingWarningCount() {
      return TestUtil.getWarningCount("WFLYUT0101", false, DEFAULT_CONTAINER_QUALIFIER);
   }

   @Deployment
   public static Archive<?> deploySimpleResource() {
      initServletWarningsCount = getServletMappingWarningCount();
      WebArchive war = ShrinkWrap.create(WebArchive.class, DuplicitePathTest.class.getSimpleName() + ".war");
      war.addClass(DuplicitePathDupliciteApplicationOne.class);
      war.addClass(DuplicitePathDupliciteApplicationTwo.class);
      war.addClass(DuplicitePathDupliciteResourceOne.class);
      war.addClass(DuplicitePathDupliciteResourceTwo.class);
      war.addClass(DuplicitePathMethodResource.class);
      war.addClass(DuplicitePathNoDupliciteApplication.class);
      return war;
   }

   @BeforeClass
   public static void init() {
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void close() {
      client.close();
      client = null;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, DuplicitePathTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Check that warning message was logged, if client makes request to path,
    * that is handled by two methods in two end-point in two application classes
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicationTwoAppTwoResourceSameMethodPath() throws Exception {
      WebTarget base = client.target(generateURL("/a/b/c"));
      Response response = null;
      try {
         response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String strResponse = response.readEntity(String.class);
         Assert.assertThat("Wrong body of response", strResponse,
               either(is(DuplicitePathDupliciteResourceOne.DUPLICITE_RESPONSE))
                     .or(is(DuplicitePathDupliciteResourceTwo.DUPLICITE_RESPONSE)));
      } finally {
         response.close();
      }
      Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1445", "Wrong count of warnings in server log"),
                     1, getServletMappingWarningCount() - initServletWarningsCount);
   }

   /**
    * @tpTestDetails Check that warning message was logged, if client makes request to path,
    * that is handled by two methods in two end-point in two application classes
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicationMoreAccepts() throws Exception {
      int initWarningsCount = getWarningCount();
      WebTarget base = client.target(generateURL("/f/g/i"));
      Response response = null;
      try {
         response = base.request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String strResponse = response.readEntity(String.class);
         Assert.assertThat("Wrong body of response", strResponse,
               is(DuplicitePathMethodResource.NO_DUPLICITE_RESPONSE));
      } finally {
         response.close();
      }
      Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-3459", "Wrong count of warnings in server log"),
                     0, getWarningCount() - initWarningsCount);
   }

   /**
    * @tpTestDetails Check that warning message was logged, if client makes request to path,
    * that is handled by two methods in two end-point in two application classes
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicationMoretypes() throws Exception {
      int initWarningsCount = getWarningCount();
      WebTarget base = client.target(generateURL("/f/g/j"));
      Response response = null;
      try {
         response = base.request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String strResponse = response.readEntity(String.class);
         Assert.assertThat("Wrong body of response", strResponse,
               is(DuplicitePathMethodResource.DUPLICITE_TYPE_GET));
      } finally {
         response.close();
      }
      Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-3459", "Wrong count of warnings in server log"),
                     0, getWarningCount() - initWarningsCount);
   }

   /**
    * @tpTestDetails Check that warning message was logged, if client makes request to path,
    * that is handled by two methods in two end-point in one application classes
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicationOneAppTwoResourcesWithSamePath() throws Exception {
      int initWarningsCount = getWarningCount();
      WebTarget base = client.target(generateURL("/f/b/c"));
      Response response = null;
      try {
         response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String strResponse = response.readEntity(String.class);
         Assert.assertThat("Wrong body of response", strResponse,
               either(is(DuplicitePathDupliciteResourceOne.DUPLICITE_RESPONSE))
                     .or(is(DuplicitePathDupliciteResourceTwo.DUPLICITE_RESPONSE)));
      } finally {
         response.close();
      }
      Assert.assertEquals("Wrong count of warnings in server log", 1, getWarningCount() - initWarningsCount);
   }

   /**
    * @tpTestDetails Check that warning message was logged, if client makes request to path, that is handled by two methods
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicationPathInMethod() throws Exception {
      int initWarningsCount = getWarningCount();
      WebTarget base = client.target(generateURL("/f/g/h"));
      Response response = null;
      try {
         response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertThat("Wrong body of response", response.readEntity(String.class),
               either(is(DuplicitePathMethodResource.DUPLICITE_RESPONSE_1))
                     .or(is(DuplicitePathMethodResource.DUPLICITE_RESPONSE_2)));
      } finally {
         response.close();
      }
      Assert.assertEquals("Wrong count of warnings in server log", 1, getWarningCount() - initWarningsCount);
   }

   /**
    * @tpTestDetails Check that warning message was not logged, if client makes request to path, that is handled by one method (correct behaviour)
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testNoDuplicationPathInMethod() throws Exception {
      int initWarningsCount = getWarningCount();
      WebTarget base = client.target(generateURL("/f/g/i"));
      Response response = null;
      try {
         response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("Wrong body of response", DuplicitePathMethodResource.NO_DUPLICITE_RESPONSE, response.readEntity(String.class));
      } finally {
         response.close();
      }
      Assert.assertEquals("Wrong count of warnings in server log", 0, getWarningCount() - initWarningsCount);
   }
}
