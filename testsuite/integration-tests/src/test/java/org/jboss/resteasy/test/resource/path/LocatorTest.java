package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.LocatorResource;
import org.jboss.resteasy.test.resource.path.resource.LocatorTestLocator;
import org.jboss.resteasy.test.resource.path.resource.LocatorTestLocator2;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class LocatorTest {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(LocatorTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, LocatorResource.class, LocatorTestLocator.class,
                LocatorTestLocator2.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, LocatorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request for the resource which is not annotated with any HTTP method anotation.
     *                This resource created new object resource and passes the request to this new created object.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocatorWithSubWithPathAnnotation() {
        Response response = client.target(generateURL("/locator/responseok/responseok")).request().get();
        Assertions.assertEquals(200, response.getStatus());
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
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }

}
