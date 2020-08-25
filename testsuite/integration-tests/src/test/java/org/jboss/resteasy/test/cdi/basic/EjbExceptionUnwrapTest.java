package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapFooException;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapFooExceptionMapper;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapFooResource;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapFooResourceBean;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapLocatingResource;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapLocatingResourceBean;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapSimpleResource;
import org.jboss.resteasy.test.cdi.basic.resource.EjbExceptionUnwrapSimpleResourceBean;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for unwrapping EJB exception
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EjbExceptionUnwrapTest {

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
      WebArchive war = TestUtil.prepareArchive(EjbExceptionUnwrapTest.class.getSimpleName());
      war.addClasses(EjbExceptionUnwrapFooException.class, EjbExceptionUnwrapFooResource.class,
            EjbExceptionUnwrapLocatingResource.class, EjbExceptionUnwrapSimpleResource.class);
      return TestUtil.finishContainerPrepare(war, null, EjbExceptionUnwrapFooExceptionMapper.class,
            EjbExceptionUnwrapSimpleResourceBean.class,
            EjbExceptionUnwrapLocatingResourceBean.class, EjbExceptionUnwrapFooResourceBean.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, EjbExceptionUnwrapTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails No default resource for exception
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testNoDefaultsResourceForException() {
      Response response = client.target(generateURL("/exception")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_CONFLICT, response.getStatus());
      response.close();
   }

   /**
    * @tpTestDetails No default resource without exception mapping
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testNoDefaultsResource() throws Exception {
      {
         Response response = client.target(generateURL("/basic")).request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         response.close();
      }
      {
         Response response = client.target(generateURL("/basic")).request().put(Entity.entity("basic", "text/plain"));
         Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
         response.close();
      }
      {
         Response response = client.target(generateURL("/queryParam")).queryParam("param", "hello world").request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.readEntity(String.class));
         response.close();
      }
      {
         Response response = client.target(generateURL("/uriParam/1234")).request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.readEntity(String.class));
         response.close();
      }
   }

   /**
    * @tpTestDetails Check for locating resource
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
      ExpectedFailingWithStandaloneMicroprofileConfiguration.class,  //  fails because /basic is not found (MP is missing EJB3)
      NotForBootableJar.class // no EJB layer so far
   })
   public void testLocatingResource() throws Exception {
      {
         Response response = client.target(generateURL("/locating/basic")).request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.readEntity(String.class));
         response.close();
      }
      {
         Response response = client.target(generateURL("/locating/basic")).request().put(Entity.entity("basic", "text/plain"));
         Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
         response.close();
      }
      {
         Response response = client.target(generateURL("/locating/queryParam")).queryParam("param", "hello world")
               .request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.readEntity(String.class));
         response.close();
      }
      {
         Response response = client.target(generateURL("/locating/uriParam/1234"))
               .request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.readEntity(String.class));
         response.close();
      }
   }
}
