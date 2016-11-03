package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.JsonMapFoo;
import org.jboss.resteasy.test.providers.jettison.resource.JsonMapResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class JsonMapTest {

    protected final Logger logger = Logger.getLogger(JsonMapTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonMapTest.class.getSimpleName());
        war.addClass(JsonMapTest.class);
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, JsonMapFoo.class, JsonMapResource.class);
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
        return PortProviderUtil.generateURL(path, JsonMapTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request for json resource. The response should be processed with jettison provider.
     * The json map is send as entity with the POST request.
     * @tpPassCrit The response returned successfully
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProvider() throws Exception {
        WebTarget target = client.target(generateURL("/map"));
        Response response = target.request().get();
        String stringResponse = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        logger.info(stringResponse);
        response.close();

        response = target.request().post(Entity.entity(stringResponse, "application/json"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        response = target.request()
                .post(Entity.json("{\"monica\":{\"jsonMapFoo\":{\"@name\":\"monica\"}},\"bill\":{\"jsonMapFoo\":{\"@name\":\"bill\"}}}"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request for json resource. The response should be processed with jettison provider.
     * The entity in the resource is empty map.
     * @tpPassCrit The response returned successfully
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyMap() throws Exception {
        WebTarget target = client.target(generateURL("/map/empty"));
        Response response = target.request().post(Entity.json("{}"));
        String responseString = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The map in the response should be empty and it's not", "{}", responseString);

    }
}
