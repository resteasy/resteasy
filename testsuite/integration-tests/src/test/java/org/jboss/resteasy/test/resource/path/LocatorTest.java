package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.LocatorResource;
import org.jboss.resteasy.test.resource.path.resource.LocatorTestLocator;
import org.jboss.resteasy.test.resource.path.resource.LocatorTestLocator2;
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
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LocatorTest {

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
        WebArchive war = TestUtil.prepareArchive(LocatorTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, LocatorResource.class, LocatorTestLocator.class, LocatorTestLocator2.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, LocatorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request for the resource which is not annotated with any HTTP method anotation.
     * This resource created new object resource and passes the request to this new created object.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocatorWithSubWithPathAnnotation() {
        Response response = client.target(generateURL("/locator/responseok/responseok")).request().get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request with the path which matches multiple resources.
     * @tpPassCrit Correct resource is used and successful response is returned.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultiplePathOptions() {
        Response response = client.target(generateURL("/resource/responseok")).request().options();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }


}
