package org.jboss.resteasy.test.resource.param;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.param.resource.MultiplePathSegmentResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for @PathParam capturing multiple PathSegments (RESTEASY-1653)
 * @tpSince RESTEasy 3.1.3.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiplePathSegmentTest {

   private static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MultiplePathSegmentTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, MultiplePathSegmentResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MultiplePathSegmentTest.class.getSimpleName());
   }

   @BeforeClass
   public static void setup() {
       client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() {
       client.close();
   }
   
   /**
    * @tpTestDetails Array of PathSegments captured by wildcard
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testWildcardArray() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/c/array/3")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails List of PathSegments captured by wildcard
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testWildcardList() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/c/list/3")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails ArrayList of PathSegments captured by wildcard
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testWildcardArrayList() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/c/arraylist/3")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails Array of PathSegments captured by two separate segments with the same name
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testTwoSegmentsArray() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/array")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails List of PathSegments captured by two separate segments with the same name
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testTwoSegmentsList() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/list")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails ArrayList of PathSegments captured by two separate segments with the same name
    * @tpSince RESTEasy 3.1.3.Final
    */
   @Test
   public void testTwoSegmentsArrayList() throws Exception
   {
      Invocation.Builder request = client.target(generateURL("/a/b/arraylist")).request();
      Response response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }
}