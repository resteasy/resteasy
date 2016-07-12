package org.jboss.resteasy.test.providers.jettison;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.JsonCollectionFoo;
import org.jboss.resteasy.test.providers.jettison.resource.JsonCollectionMyNamespacedResource;
import org.jboss.resteasy.test.providers.jettison.resource.JsonCollectionMyResource;
import org.jboss.resteasy.test.providers.jettison.resource.JsonCollectionNamespacedFoo;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonCollectionTest {

    protected final Logger logger = Logger.getLogger(JsonCollectionTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonCollectionTest.class.getSimpleName());
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, JsonCollectionFoo.class, JsonCollectionMyNamespacedResource.class,
                JsonCollectionNamespacedFoo.class, JsonCollectionMyResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonCollectionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test that an array containing json objects can be received and send
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testArray() throws Exception {
        WebTarget target = client.target(generateURL("/array"));
        Response response;
        {
            response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            logger.info(response.readEntity(String.class));
        }

        {
            response = target.request()
                    .post(Entity.entity("[{\"jsonCollectionFoo\":{\"@test\":\"bill{\"}},{\"jsonCollectionFoo\":{\"@test\":\"monica\\\"}\"}}]",
                            "application/json"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            logger.info(response.readEntity(String.class));
        }

    }

    /**
     * @tpTestDetails Test that json entity is correctly marshalled into the array, and json string is returned from the server
     * and the same entity is send back to the server to produce a list and returned as json string.
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {

        WebTarget targetArray = client.target(generateURL("/array"));
        Response response = targetArray.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info(entity);

        WebTarget targetList = client.target(generateURL("/list"));
        response = targetList.request().post(Entity.entity(entity, "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not the expected one",
                "[{\"jsonCollectionFoo\":{\"@test\":\"bill{\"}},{\"jsonCollectionFoo\":{\"@test\":\"monica\\\"}\"}}]",
                response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test that an array of namespaced json objects can be received and send
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedArray() throws Exception {
        WebTarget target = client.target(generateURL("/namespaced/array"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info(entity);

        response = target.request().post(Entity.entity(entity, "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not the expected one",
                "[{\"foo.com.foo\":{\"@test\":\"bill{\"}},{\"foo.com.foo\":{\"@test\":\"monica\\\"}\"}}]",
                response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test that namespaced json entity is correctly marshalled into the array, and json string is returned from the server
     * and the same entity is send back to the server to produce a list and returned as json string.
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedList() throws Exception {
        WebTarget target = client.target(generateURL("/namespaced/array"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info(entity);

        WebTarget targetList = client.target(generateURL("/namespaced/list"));
        response = targetList.request().post(Entity.entity(entity, "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not the expected one",
                "[{\"foo.com.foo\":{\"@test\":\"bill{\"}},{\"foo.com.foo\":{\"@test\":\"monica\\\"}\"}}]",
                response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test to send and receive empty json array
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyArray() throws Exception {
        WebTarget target = client.target(generateURL("/empty/array"));
        Response response = target.request().post(Entity.entity("[]", "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not the expected one", "[]", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test to send and receive empty json list
     * @tpPassCrit The response with Success is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyList() throws Exception {
        WebTarget target = client.target(generateURL("/empty/list"));
        Response response = target.request().post(Entity.entity("[]", "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response from the server is not the expected one", "[]", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test to send non-json input
     * @tpPassCrit The response with code Bad request is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadArray() throws Exception {
        WebTarget target = client.target(generateURL("/array"));
        Response response = target.request().post(Entity.entity("asdfasdfasdf", "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
    }
}
