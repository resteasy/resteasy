package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.basic.resource.CDILocatorResource;
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
import javax.ws.rs.client.WebTarget;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for CDI locator
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @BeforeClass
    public static void initClient() {
        client = ClientBuilder.newClient();
        baseTarget = client.target(generateURL());
    }

    @AfterClass
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
        Assert.assertEquals("Wrong response", "OK", result);
    }

    /**
     * @tpTestDetails Check locator
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void locatorTest() throws Exception {
        String result = baseTarget.path("test/lookup").queryParam("foo", "yo").request().get(String.class);
        Assert.assertEquals("Wrong response", "OK", result);
    }
}
