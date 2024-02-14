package org.jboss.resteasy.test.providers.jaxb;

import static org.hamcrest.CoreMatchers.containsString;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.CollectionCustomer;
import org.jboss.resteasy.test.providers.jaxb.resource.CollectionNamespacedCustomer;
import org.jboss.resteasy.test.providers.jaxb.resource.CollectionNamespacedResource;
import org.jboss.resteasy.test.providers.jaxb.resource.CollectionResource;
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
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check jaxb requests with collection
 * @tpSince RESTEasy 3.0.16
 */
//@Disabled("RESTEASY-3450")
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CollectionCoreTest {
    private static final String WRONG_RESPONSE = "Response contains wrong data";
    protected static final Logger logger = Logger.getLogger(CollectionCoreTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CollectionCoreTest.class.getSimpleName());
        war.addClasses(CollectionCustomer.class, CollectionNamespacedCustomer.class);
        return TestUtil.finishContainerPrepare(war, null, CollectionResource.class, CollectionNamespacedResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CollectionCoreTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test array response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testArray() throws Exception {
        Invocation.Builder request = client.target(generateURL("/array")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(String.format("Response: %s", str));
        response.close();
        response = request.put(Entity.entity(str, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test list response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {
        Invocation.Builder request = client.target(generateURL("/list")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(String.format("Response: %s", str));
        response.close();
        response = request.put(Entity.entity(str, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test GenericEntity of list response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResponse() throws Exception {
        Invocation.Builder request = client.target(generateURL("/list/response")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info(String.format("Response: %s", response.readEntity(String.class)));
    }

    /**
     * @tpTestDetails Test array of customers with namespace in XML
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedArray() throws Exception {
        Invocation.Builder request = client.target(generateURL("/namespaced/array")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(String.format("Response: %s", str));
        response.close();
        response = request.put(Entity.entity(str, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
        MatcherAssert.assertThat(WRONG_RESPONSE, str, containsString("http://customer.com"));
    }

    /**
     * @tpTestDetails Test GenericEntity with list of customers with namespace in XML
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedList() throws Exception {
        Invocation.Builder request = client.target(generateURL("/namespaced/list")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(String.format("Response: %s", str));
        response.close();
        response = request.put(Entity.entity(str, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
        MatcherAssert.assertThat(WRONG_RESPONSE, str, containsString("http://customer.com"));
    }

    /**
     * @tpTestDetails Test list of customers with namespace in XML
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedResponse() throws Exception {
        Invocation.Builder request = client.target(generateURL("/namespaced/list/response")).request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String str = response.readEntity(String.class);
        logger.info(String.format("Response: %s", str));
        response.close();
        MatcherAssert.assertThat(WRONG_RESPONSE, str, containsString("http://customer.com"));
    }

}
