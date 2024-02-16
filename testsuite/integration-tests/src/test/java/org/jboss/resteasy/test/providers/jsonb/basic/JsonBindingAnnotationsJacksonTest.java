package org.jboss.resteasy.test.providers.jsonb.basic;

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Cat;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingCustomRepeaterProvider;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.JsonBindingResource;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Json-binding provider.
 * @tpChapter Integration test
 * @tpSince RESTEasy 3.5
 */
//@Disabled("RESTEASY-3450")
@ExtendWith(ArquillianExtension.class)
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
        } else {
            war.addAsManifestResource("jboss-deployment-structure-no-json-b.xml", "jboss-deployment-structure.xml");
        }
        return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class, Cat.class);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonBindingAnnotationsJacksonTest.class.getSimpleName());
        war.addClass(JsonBindingAnnotationsJacksonTest.class);
        return TestUtil.finishContainerPrepare(war, null, JsonBindingResource.class, Cat.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
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
        MatcherAssert.assertThat("Variable with JsonbTransient annotation should be transient, if JSON-B is used",
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
        WebTarget target = client
                .target(PortProviderUtil.generateURL("/test/jsonBinding/cat/not/transient", WAR_WITHOUT_JSONB));
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
        Entity<Cat> entity = Entity.entity(
                new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
        Cat json = target.request().post(entity, Cat.class);
        logger.info("Request entity: " + entity);
        MatcherAssert.assertThat("Variable with JsonbTransient annotation should not be transient, if JSON-B is not used",
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
        LogCounter errorLogCounter = new LogCounter("ERROR", false, DEFAULT_CONTAINER_QUALIFIER);
        try {
            Client client = ClientBuilder.newBuilder().register(JsonBindingCustomRepeaterProvider.class).build();
            String charset = "UTF-8";
            WebTarget target = client.target(PortProviderUtil.generateURL("/test/jsonBinding/repeater", WAR_WITH_JSONB));
            MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset(charset);
            Entity<Cat> entity = Entity.entity(
                    new Cat("Rosa", "semi-british", "tabby", true, JsonBindingResource.CLIENT_TRANSIENT_VALUE), mediaType);
            logger.info("Request entity: " + entity);
            Response response = target.request().post(entity);
            // check server logs
            MatcherAssert.assertThat("Server printed more than one error message during the request",
                    errorLogCounter.count(),
                    is(1));
            // check response
            int responseCode = response.getStatus();
            MatcherAssert.assertThat("Wrong response code", responseCode, is(500));
            String responseBody = response.readEntity(String.class);
            Assertions.assertTrue(responseBody.matches(".*RESTEASY008200:.*jakarta.json.bind.JsonbException.*"),
                    "Wrong response error message: " + responseBody);
        } finally {
            client.close();
        }
    }
}
