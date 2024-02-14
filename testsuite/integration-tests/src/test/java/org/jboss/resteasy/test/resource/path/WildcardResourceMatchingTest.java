package org.jboss.resteasy.test.resource.path;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.path.resource.WildcardMatchingResource;
import org.jboss.resteasy.test.resource.path.resource.WildcardMatchingSubResource;
import org.jboss.resteasy.test.resource.path.resource.WildcardMatchingSubSubResource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Check class name of sub-resources, which process client request
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class WildcardResourceMatchingTest {

    static Client client;

    @Deployment(name = "UriInfoSimpleResource")
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(WildcardResourceMatchingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WildcardMatchingResource.class,
                WildcardMatchingSubResource.class, WildcardMatchingSubSubResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WildcardResourceMatchingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check root resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMain() {
        Response response = client.target(generateURL("/main")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("WildcardMatchingResource", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Check sub-resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMainSub() {
        Response response = client.target(generateURL("/main/sub")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("WildcardMatchingSubResource", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Check sub-sub-resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMainSubSub() {
        Response response = client.target(generateURL("/main/sub/sub")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("WildcardMatchingSubSubResource", response.readEntity(String.class));
        response.close();
    }

}
