package org.jboss.resteasy.test.validation;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.validation.resource.ManagedBeanValidationApplication;
import org.jboss.resteasy.test.validation.resource.ManagedBeanValidationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Verify class / property validation occurs for JNDI resources.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1203
 * @tpSince RESTEasy 3.12.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ManagedBeanValidationTest {

    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchiveWithApplication(ManagedBeanValidationTest.class.getSimpleName(),
                ManagedBeanValidationApplication.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, ManagedBeanValidationResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ManagedBeanValidationTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeClass() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterClass() {
        client.close();
    }

    /**
     * @tpTestDetails Verify behavior for valid parameters.
     * @tpSince RESTEasy 3.12.0
     */
    @Test
    public void testManagedBeanValidationValid() {
        Response response = client.target(generateURL("/validate")).queryParam("q", 2).request().get();
        Assertions.assertEquals(200, response.getStatus());
        response = client.target(generateURL("/visited")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.readEntity(boolean.class));
    }

    /**
     * @tpTestDetails Verify behavior for invalid parameters.
     * @tpSince RESTEasy 3.12.0
     */
    @Test
    public void testManagedBeanValidationInvalid() {
        Response response = client.target(generateURL("/validate")).queryParam("q", 0).request().get();
        Assertions.assertEquals(400, response.getStatus());
        response = client.target(generateURL("/visited")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.readEntity(boolean.class));
    }
}
