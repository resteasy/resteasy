package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.ContextAfterEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextBeforeEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextEncoderInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextEndInterceptor;
import org.jboss.resteasy.test.core.basic.resource.ContextService;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.core.Response;
import java.io.FilePermission;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-699
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContextTest {
   public static final String WRONG_RESPONSE_ERROR_MSG = "Wrong content of response";

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, ContextTest.class.getSimpleName() + ".war");
      war.addClasses(ContextAfterEncoderInterceptor.class, ContextBeforeEncoderInterceptor.class, ContextService.class,
            ContextEncoderInterceptor.class, ContextEndInterceptor.class);
      war.addAsWebInfResource(ContextTest.class.getPackage(), "ContextIndex.html", "index.html");
      war.addAsWebInfResource(ContextTest.class.getPackage(), "ContextWeb.xml", "web.xml");
      // undertow requires read permission in order to perform forward request.
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new FilePermission("<<ALL FILES>>", "read")
         ), "permissions.xml");
      return war;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ContextTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Test for forwarding request to external HTML file
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testForward() throws Exception {
      Response response = client.target(generateURL("/test/forward")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong content of response", "hello world", response.readEntity(String.class));
      response.close();
   }

   /**
    * @tpTestDetails Base URL should not be affected by URL parameter
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testRepeat() throws Exception {
      Response response = client.target(generateURL("/test/test")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Resource get wrong injected URL", generateURL("/test/"), response.readEntity(String.class));
      response.close();
      response = client.target(generateURL("/test/")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Resource get wrong injected URL", generateURL("/test/"), response.readEntity(String.class));
      response.close();
   }

   /**
    * @tpTestDetails Test for getting servlet context in REST resource
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testServletContext() throws Exception {
      final String HEADER_ERROR_MESSAGE = "Response don't have correct headers";
      Response response = client.target(generateURL("/test/test/servletcontext")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "ok", response.readEntity(String.class));
      Assert.assertNotNull(HEADER_ERROR_MESSAGE, response.getHeaderString("before-encoder"));
      Assert.assertNotNull(HEADER_ERROR_MESSAGE, response.getHeaderString("after-encoder"));
      Assert.assertNotNull(HEADER_ERROR_MESSAGE, response.getHeaderString("end"));
      Assert.assertNotNull(HEADER_ERROR_MESSAGE, response.getHeaderString("encoder"));
      response.close();
   }

   /**
    * @tpTestDetails Test for getting servlet config in REST resource
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testServletConfig() throws Exception {
      Response response = client.target(generateURL("/test/test/servletconfig")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "ok", response.readEntity(String.class));
      response.close();
   }

   /**
    * @tpTestDetails XML extension mapping test
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testXmlMappings() throws Exception {
      Response response = client.target(generateURL("/test/stuff.xml")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "xml", response.readEntity(String.class));
      response.close();

   }

   /**
    * @tpTestDetails Json extension mapping test
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJsonMappings() throws Exception {
      Response response = client.target(generateURL("/test/stuff.json")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "json", response.readEntity(String.class));
      response.close();
   }
}
