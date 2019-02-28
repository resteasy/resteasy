package org.jboss.resteasy.test.providers.jackson2;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceJacksonResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceJacksonXmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceProduct2;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceXmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceXmlProduct;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@Ignore
@RunWith(Arquillian.class)
@RunAsClient
public class JacksonJaxbCoexistenceTest {

   static ResteasyClient client;
   protected static final Logger logger = Logger.getLogger(JacksonJaxbCoexistenceTest.class.getName());

   @Deployment(name = "default")
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(JacksonJaxbCoexistenceTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, JacksonJaxbCoexistenceJacksonResource.class,
            JacksonJaxbCoexistenceJacksonXmlResource.class, JacksonJaxbCoexistenceXmlResource.class,
            JacksonJaxbCoexistenceProduct2.class,
            JacksonJaxbCoexistenceXmlProduct.class);
   }

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JacksonJaxbCoexistenceTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Client sends GET request for json annotated resource. In the first case it returns single json entity,
    * in the second case multiple json entities as String.
    * @tpPassCrit The resource returns json entities in correct format
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJacksonString() throws Exception {
      WebTarget target = client.target(generateURL("/products/333"));
      Response response = target.request().get();
      String entity = response.readEntity(String.class);
      logger.info(entity);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("The response entity content doesn't match the expected",
            "{\"name\":\"Iphone\",\"id\":333}", entity);

      response.close();

      target = client.target(generateURL("/products"));
      response = target.request().get();
      entity = response.readEntity(String.class);
      logger.info(entity);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("The response entity content doesn't match the expected",
            "[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);
      response.close();
   }

   /**
    * @tpTestDetails Test that Jackson is picked
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJacksonXmlString() throws Exception {
      WebTarget target = client.target(generateURL("/jxml/products/333"));
      Response response = target.request().get();
      String entity = response.readEntity(String.class);
      logger.info(entity);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("The response entity content doesn't match the expected",
            "{\"name\":\"Iphone\",\"id\":333}", entity);
      response.close();

      target = client.target(generateURL("/jxml/products"));
      response = target.request().get();
      entity = response.readEntity(String.class);
      logger.info(entity);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("The response entity content doesn't match the expected",
            "[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);
      response.close();
   }

   /**
    * @tpTestDetails Test that Jackson is picked and object can be returned from the resource
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJackson() throws Exception {
      WebTarget target = client.target(generateURL("/products/333"));
      Response response = target.request().get();
      JacksonJaxbCoexistenceProduct2 p = response.readEntity(JacksonJaxbCoexistenceProduct2.class);
      Assert.assertEquals("JacksonJaxbCoexistenceProduct id value doesn't match", 333, p.getId());
      Assert.assertEquals("JacksonJaxbCoexistenceProduct name value doesn't match", "Iphone", p.getName());
      response.close();

      target = client.target(generateURL("/products"));
      response = target.request().get();
      String entity = response.readEntity(String.class);
      logger.info(entity);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();

      target = client.target(generateURL("/products/333"));
      response = target.request().post(Entity.entity(p, "application/foo+json"));
      p = response.readEntity(JacksonJaxbCoexistenceProduct2.class);
      Assert.assertEquals("JacksonJaxbCoexistenceProduct id value doesn't match", 333, p.getId());
      Assert.assertEquals("JacksonJaxbCoexistenceProduct name value doesn't match", "Iphone", p.getName());
   }
}
