package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.ResourceInfoInjectionFilter;
import org.jboss.resteasy.test.resource.basic.resource.ResourceInfoInjectionResource;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4701
 * @tpSince RESTEasy 3.0.17
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceInfoInjectionTest {
    protected static Client client;

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceInfoInjectionTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ResourceInfoInjectionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResourceInfoInjectionFilter.class,
                ResourceInfoInjectionResource.class);
    }

    /**
     * @tpTestDetails Check for injecting ResourceInfo object in ContainerResponseFilter
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNotFound() throws Exception {
        WebTarget target = client.target(generateURL("/bogus"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND * 2,
                response.getStatus(), "ResponseFilter was probably not applied to response");
        Assertions.assertTrue(entity.isEmpty(), "Wrong body of response");
    }

    /**
     * @tpTestDetails Check for injecting ResourceInfo object in end-point
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testAsync() throws Exception {
        WebTarget target = client.target(generateURL("/async"));
        Response response = target.request().post(Entity.entity("hello", "text/plain"));
        String val = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                "OK status is expected");
        Assertions.assertEquals("async", val, "Wrong body of response");
    }
}
