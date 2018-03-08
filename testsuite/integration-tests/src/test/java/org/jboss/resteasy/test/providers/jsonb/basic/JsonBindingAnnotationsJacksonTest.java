package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Cat;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingCustomRepeaterProvider;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingResource;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter Json-binding provider.
 * @tpChapter Integration test
 * @tpSince RESTEasy 3.5
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonBindingAnnotationsJacksonTest {


   private static final String WAR_WITH_JSONB = "war_with_jsonb";
   private static final String WAR_WITHOUT_JSONB = "war_without_jsonb";

   protected static final Logger logger = Logger.getLogger(JsonBindingAnnotationsJacksonTest.class.getName());

   static Client client;

   @Deployment(name = WAR_WITH_JSONB)
   public static Archive<?> deployWithJsonB() {
      return deploy(WAR_WITH_JSONB, true);
   }

   @Deployment(name = WAR_WITHOUT_JSONB)
   public static Archive<?> deployWithoutJsonB() {
      return deploy(WAR_WITHOUT_JSONB, false);
   }

   public static Archive<?> deploy(String archiveName, boolean useJsonB) {
      WebArchive war = TestUtil.prepareArchive(archiveName);
      war.addClass(JsonBindingTest.class);
      if (useJsonB) {
         war.addAsManifestResource("jboss-deployment-structure-json-b.xml", "jboss-deployment-structure.xml");
      }
      return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class, Cat.class);
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(JsonBindingAnnotationsJacksonTest.class.getSimpleName());
      war.addClass(JsonBindingAnnotationsJacksonTest.class);
      return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class, Cat.class);
   }

   @Before
   public void init() {
      client = ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
      client = null;
   }

   /**
    * @tpTestDetails JSON-B is not used on client, JSON-B is used on server
    *                client send a value in variable with @JsonbTransient annotation
    *                server should not receive a value in this variable (JSON-B on server should filter it)
    *                end-point returns a value in this variable, but server should ignore this variable
    *                check that server returns object without variable with @JsonbTransient annotation to client
    *
    * @tpPassCrit The resource returns object with correct values
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void jsonbOnServerNotOnClientTest() throws Exception {
      String charset = "UTF-8";
      WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/cat/transient", WAR_WITH_JSONB));
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
      Entity<Cat> entity = Entity.entity(
              new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
      Cat json = target.request().post(entity, Cat.class);
      logger.info("Request entity: " + entity);
      Assert.assertThat("Variable with JsonbTransient annotation should be transient, if JSON-B is used",
              json.getTransientVar(), is(Cat.DEFAULT_TRANSIENT_VAR_VALUE));
   }
   /**
    * @tpTestDetails JSON-B is not used on both server and client
    *                check that @JsonbTransient annotation is ignored
    *
    * @tpPassCrit The resource returns object with correct values
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void jsonbNotOnServerNotOnClientTest() throws Exception {
      String charset = "UTF-8";
      WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/cat/not/transient", WAR_WITHOUT_JSONB));
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
      Entity<Cat> entity = Entity.entity(
              new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
      Cat json = target.request().post(entity, Cat.class);
      logger.info("Request entity: " + entity);
      Assert.assertThat("Variable with JsonbTransient annotation should not be transient, if JSON-B is not used",
              json.getTransientVar(), is(JsonBindingResource.RETURNED_TRANSIENT_VALUE));
   }

   /**
    * @tpTestDetails JSON-B is not used on client, JSON-B is used on server
    *                client uses custom json provider that returns corrupted json data
    *                client sends corrupted json data to server
    *                JSON-B provider on server should throw relevant exception
    *                Server should returns relevant error message in response
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void negativeScenarioOnServer() throws Exception {

      try {
         ResteasyClient client = new ResteasyClientBuilder().register(JsonBindingCustomRepeaterProvider.class).build();
         String charset = "UTF-8";
         WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/repeater", WAR_WITH_JSONB));
         MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
         Entity<Cat> entity = Entity.entity(
                 new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
         logger.info("Request entity: " + entity);
         Response response = target.request().post(entity);
         // check response
         int responseCode = response.getStatus();
         Assert.assertThat("Wrong response code", responseCode, is(400));
         String responseBody = response.readEntity(String.class);
         Assert.assertTrue("Wrong response error message: " + responseBody,
                 responseBody.startsWith("javax.ws.rs.ProcessingException: RESTEASY008200"));
      } finally {
         client.close();
      }
   }
}
