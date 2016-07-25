package org.jboss.resteasy.test.providers.map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.map.resource.MapProviderBuiltInResource;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MapProviderBuiltInTest {

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
        WebArchive war = TestUtil.prepareArchive(MapProviderBuiltInTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MapProviderBuiltInResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MapProviderBuiltInTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client creates request of type "POST" with entity of type MultiValuesMap and sends it to the
     * server using invocation method. The server returns response containing MultiValuedMap. The builtin Resteasy MapProvider
     * is used for reading request and writing response.
     * @tpPassCrit Correct response is returned from the server and map contains original item
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapInvoke() {
        // writers sorted by type, mediatype, and then by app over builtin
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add("map", "map");
        Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .build("POST", Entity.entity(map, MediaType.APPLICATION_FORM_URLENCODED)).invoke();
        Assert.assertEquals(response.getStatus(), 200);
        String data = response.readEntity(String.class);
        Assert.assertTrue(data.contains("map"));
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with specified mediatype and entity of type APPLICATION_FORM_URLENCODED_TYPE
     * using post method. The server returns response containing MultiValuedMap. The builtin Resteasy MapProvider
     * is used for reading request and writing response.
     * @tpPassCrit Correct response is returned from the server and map contains original item
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapPost() {
        // writers sorted by type, mediatype, and then by app over builtin
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add("map", "map");
        Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.entity(map, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        Assert.assertEquals(response.getStatus(), 200);
        String data = response.readEntity(String.class);
        Assert.assertTrue(data.contains("map"));
        response.close();
    }


}
