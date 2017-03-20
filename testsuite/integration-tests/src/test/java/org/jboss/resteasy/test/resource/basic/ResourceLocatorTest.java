package org.jboss.resteasy.test.resource.basic;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorAbstractAnnotationFreeResouce;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorAnnotationFreeSubResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorBaseResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorCollectionResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorDirectory;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorQueueReceiver;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorReceiver;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorRootInterface;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubInterface;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource2;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource3;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource3Interface;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests path encoding
 * @tpSince RESTEasy 3.0.20
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceLocatorTest
{
   static Client client;

   @BeforeClass
   public static void setup() {
       client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
       client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(ResourceLocatorTest.class.getSimpleName());
       war.addClass(ResourceLocatorQueueReceiver.class)
          .addClass(ResourceLocatorReceiver.class)
          .addClass(ResourceLocatorRootInterface.class)
          .addClass(ResourceLocatorSubInterface.class)
          .addClass(ResourceLocatorSubresource3Interface.class)
          ;
       return TestUtil.finishContainerPrepare(war, null,
             ResourceLocatorAbstractAnnotationFreeResouce.class,
             ResourceLocatorAnnotationFreeSubResource.class,
             ResourceLocatorBaseResource.class,
             ResourceLocatorCollectionResource.class,
             ResourceLocatorDirectory.class,
             ResourceLocatorSubresource.class,
             ResourceLocatorSubresource2.class,
             ResourceLocatorSubresource3.class
             );
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, ResourceLocatorTest.class.getSimpleName());
   }


   /**
    * @tpTestDetails Resource locator returns proxied resource.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testProxiedSubresource() throws Exception
   {
      WebTarget target = client.target(generateURL("/proxy/3"));
      Response res = target.queryParam("foo", "1.2").queryParam("foo", "1.3").request().get();
      Assert.assertEquals(200, res.getStatus());
      res.close();
   }


   /**
    * @tpTestDetails 1) Resource locator returns resource; 2) Resource locator returns resource locator.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testSubresource() throws Exception
   {
      {
         Response response = client.target(generateURL("/base/1/resources")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(ResourceLocatorSubresource.class.getName(), response.readEntity(String.class));
      }

      {
         Response response = client.target(generateURL("/base/1/resources/subresource2/stuff/2/bar")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(ResourceLocatorSubresource2.class.getName() + "-2", response.readEntity(String.class));
      }
   }


   /**
    * @tpTestDetails Two matching metods, one a resource locator, the other a resource method.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testSameUri() throws Exception
   {
      Response response = client.target(generateURL("/directory/receivers/1")).request().delete();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals(ResourceLocatorDirectory.class.getName(), response.readEntity(String.class));
   }


   /**
    * @tpTestDetails Locator returns resource which inherits annotations from an interface.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testAnnotationFreeSubresource() throws Exception
   {
      {
         Response response = client.target(generateURL("/collection/annotation_free_subresource")).request().get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("got", response.readEntity(String.class));
         Assert.assertNotNull(response.getHeaderString("Content-Type"));
         Assert.assertNotNull(response.getHeaderString("Content-Type"));
         Assert.assertEquals(MediaType.TEXT_PLAIN+";charset=UTF-8", response.getHeaderString("Content-Type"));
      }

      {
         Builder request = client.target(generateURL("/collection/annotation_free_subresource")).request();
         Response response = request.post(Entity.entity("hello!".getBytes(), MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("posted: hello!", response.readEntity(String.class));
      }
   }
}
