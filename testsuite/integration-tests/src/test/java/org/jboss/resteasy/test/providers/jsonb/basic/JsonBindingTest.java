package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Cat;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
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

/**
 * @tpSubChapter Json-binding provider.JAX-RS 2.1 spec (JSR-370), section 11.2.7 states,
 * "Note that if JSON-B and JSON-P are both supported in the same environment, entity providers for
 * JSON-B take precedence over those for JSON-P for all types except JsonValue and its sub-types."
 * The sub-types of JsonValue are JsonArray, JsonNumber, JsonObject, JsonString, JsonStructure.
 * Resteasy's JSON-P providers currently supports this.  A general object such as Cat will be processed by
 * the JSON-B provider.
 * @tpChapter Integration test
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonBindingTest {

   protected static final Logger logger = Logger.getLogger(JsonBindingTest.class.getName());

   static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(JsonBindingTest.class.getSimpleName());
      war.addClass(JsonBindingTest.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
              + "Dependencies: org.jboss.resteasy.resteasy-json-binding-provider services\n"));
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

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JsonBindingTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Client sends POST request with a JSON annotated entity. The object should be returned back by the
    * response and should contain the same field values as original request.
    *
    * @tpPassCrit The resource returns object with correct values
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void doTestCat() throws Exception {
      String charset = "UTF-8";
      WebTarget target = client.target(generateURL("/test/jsonBinding/cat"));
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
      Entity<Cat> entity = Entity.entity(
              new Cat("Rosa", "semi-british", "tabby", true), mediaType);
      Cat json = target.request().post(entity, Cat.class);
      logger.info("Request entity: " + entity);
      Assert.assertTrue("Failed to return the correct name", "Alfred".equals(json.getName()));
      String jsonbResponse = target.request().post(entity).readEntity(String.class);
      Assert.assertEquals("JsonBindingProvider is not enabled", "{\"color\":\"ginger\",\"sort\":\"semi-british\",\"name\":\"Alfred\",\"domesticated\":true}", jsonbResponse);
   }
}