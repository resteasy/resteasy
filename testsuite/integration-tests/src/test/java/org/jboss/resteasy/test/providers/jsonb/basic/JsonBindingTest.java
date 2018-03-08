package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Cat;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingCustomRepeaterProvider;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

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

   private static final String WAR_WITH_JSONB = "war_with_jsonb";
   private static final String CUSTOM_JSON_PROVIDER = "custom_json_provider";

   @Deployment(name = WAR_WITH_JSONB)
   public static Archive<?> deployWithJsonB() {
      return deploy(WAR_WITH_JSONB, true);
   }

   @Deployment(name = CUSTOM_JSON_PROVIDER)
   public static Archive<?> deployWithoutJsonB() {
      return deploy(CUSTOM_JSON_PROVIDER, false);
   }

   public static Archive<?> deploy(String archiveName, boolean useJsonB) {
      WebArchive war = TestUtil.prepareArchive(archiveName);
      war.addClass(JsonBindingTest.class);
      war.addClass(Cat.class);
      if (useJsonB) {
         war.addAsManifestResource("jboss-deployment-structure-json-b.xml", "jboss-deployment-structure.xml");
         return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class);
      }
      return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class, JsonBindingCustomRepeaterProvider.class);
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
      client = null;
   }

   /**
    * @tpTestDetails Client sends POST request with a JSON annotated entity. The object should be returned back by the
    * response and should contain the same field values as original request.
    *
    * JSON-B is activated on both server and client side
    * Client should not ignore @JsonbTransient annotation and should not send a value in this variable
    * Check that server returns object without a value in variable with @JsonbTransient annotation
    *
    * @tpPassCrit The resource returns object with correct values
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void jsonbOnServerAndClientTest() throws Exception {
      String charset = "UTF-8";
      WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/cat/transient", WAR_WITH_JSONB));
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
      Entity<Cat> entity = Entity.entity(
              new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
      Cat json = target.request().post(entity, Cat.class);
      logger.info("Request entity: " + entity);
      Assert.assertTrue("Failed to return the correct name", "Alfred".equals(json.getName()));
      Assert.assertThat("Variable with JsonbTransient annotation should be transient, if JSON-B is used",
              json.getTransientVar(), is(Cat.DEFAULT_TRANSIENT_VAR_VALUE));

      String jsonbResponse = target.request().post(entity).readEntity(String.class);
      Assert.assertEquals("JsonBindingProvider is not enabled", "{\"color\":\"ginger\",\"sort\":\"semi-british\",\"name\":\"Alfred\",\"domesticated\":true}", jsonbResponse);
   }

    /**
     * @tpTestDetails JSON-B is used on client, JSON-B is not used on server, server uses test's custom json provider
     *                client should not ignore @JsonbTransient annotation and should not send a value in this variable
     *                server verify that client doesn't sent a value in a variable with @JsonbTransient annotation
     *                server returns json data with a value in a variable with @JsonbTransient annotation
     *                client should not ignore @JsonbTransient annotation and should not receive a value in this variable
     *
     * @tpPassCrit The resource returns object with correct values
     * @tpSince RESTEasy 3.5
     */
   @Test
   public void jsonbOnClientTest() throws Exception {
      String charset = "UTF-8";
      WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/client/test/transient", CUSTOM_JSON_PROVIDER));
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
      Entity<Cat> entity = Entity.entity(
              new Cat("Rosa", "semi-british", "tabby", true,
                      JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
      Cat response = target.request().post(entity, Cat.class);
      Assert.assertThat("Failed to return the correct name", response.getName(), is("Rosa"));
      Assert.assertThat("Variable with JsonbTransient annotation should be transient, if JSON-B is used",
             response.getTransientVar(), is(Cat.DEFAULT_TRANSIENT_VAR_VALUE));
   }


   /**
    * @tpTestDetails JSON-B is used on client, JSON-B is not used on server, server uses test's custom json provider
    *                Client send GET request to server
    *                Server returns Cat object, custom provider uses toString method, that doesn't doesn't create correct JSON data
    *                Client receive data with "json" media type, but data was created by toString method
    *                JSON-B on client should throw user-friendly exception, because toString method doesn't create correct JSON data
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void negativeScenarioOnClient() throws Exception {
      // call and log get request
      WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/get/cat", CUSTOM_JSON_PROVIDER));
      Response response = target.request().get();
      String responseAsString = response.readEntity(String.class);
      Assert.assertThat("Server should use custom JSON provider", responseAsString, containsString(Cat.CUSTOM_TO_STRING_FORMAT));
      logger.info("Response as a String: " + responseAsString);
      response.close();

      // call get request, try to get Cat data
      response = target.request().get();
      try {
         Cat wrongObject = response.readEntity(Cat.class);
         logger.info("JSON-B parse server toString method, although JSON-B should not do that. Received object:");
         logger.info(wrongObject.toString());;
         Assert.fail("Client should throw exception because JSON-B should not be able to parse wrong data");
      }
      catch (Throwable e) {
         StringWriter errors = new StringWriter();
         e.printStackTrace(new PrintWriter(errors));
         String stackTraceString = errors.toString();
         logger.info("StackTrace of exception:");
         logger.info(stackTraceString);
         for (String stackTraceLine : stackTraceString.split(System.lineSeparator())) {
            Assert.assertThat("User-unfriendly error message in JSON-B", stackTraceLine,
                    not(containsString("Messages (implementation not found)")));
         }
      }
   }
}
