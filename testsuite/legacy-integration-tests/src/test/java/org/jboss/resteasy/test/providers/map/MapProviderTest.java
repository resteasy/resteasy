package org.jboss.resteasy.test.providers.map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.map.resource.MapProvider;
import org.jboss.resteasy.test.providers.map.resource.MapProviderAbstractProvider;
import org.jboss.resteasy.test.providers.map.resource.MapProviderResource;
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
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MapProviderTest {

    static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MapProviderTest.class.getSimpleName());
        war.addClasses(MapProviderAbstractProvider.class);
        return TestUtil.finishContainerPrepare(war, null, MapProviderResource.class, MapProvider.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MapProviderTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends POST request with specified mediatype and entity of type APPLICATION_FORM_URLENCODED_TYPE.
     * This entity is read by application provided MapProvider, which creates Multivaluedmap and adds item into it.
     * Server sends response using application provided MapProvider, replacing content of the first item in the map.
     * @tpPassCrit Correct response is returned from the server and map contains replaced item
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapProvider() {
        // writers sorted by type, mediatype, and then by app over builtin
        Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.entity("map", MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        Assert.assertEquals(response.getStatus(), 200);
        String data = response.readEntity(String.class);
        Assert.assertTrue(data.contains("MapWriter"));
        response.close();
    }

}
