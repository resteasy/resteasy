package org.jboss.resteasy.test.cdi.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.cdi.basic.resource.CDILocatorResource;
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
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for CDI locator
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CDILocatorTest {
    static Client client;
    static WebTarget baseTarget;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CDILocatorTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CDILocatorResource.class);
    }

    private static String generateURL() {
        return PortProviderUtil.generateBaseUrl(CDILocatorTest.class.getSimpleName());
    }

    @BeforeAll
    public static void initClient() {
        client = ClientBuilder.newClient();
        baseTarget = client.target(generateURL());
    }

    @AfterAll
    public static void closeClient() {
        client.close();
    }

    /**
     * @tpTestDetails Check generic type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void genericTypeTest() throws Exception {
        String result = baseTarget.path("test").queryParam("foo", "yo").request().get(String.class);
        Assertions.assertEquals("OK", result, "Wrong response");
    }

    /**
     * @tpTestDetails Check locator
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void locatorTest() throws Exception {
        String result = baseTarget.path("test/lookup").queryParam("foo", "yo").request().get(String.class);
        Assertions.assertEquals("OK", result, "Wrong response");
    }
}
