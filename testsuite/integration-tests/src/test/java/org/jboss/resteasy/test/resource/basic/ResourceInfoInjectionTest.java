package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.resource.basic.resource.ResourceInfoInjectionFilter;
import org.jboss.resteasy.test.resource.basic.resource.ResourceInfoInjectionResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4701
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceInfoInjectionTest {
    protected static Client client;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
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
    @Category({NotForForwardCompatibility.class})
    public void testNotFound() throws Exception {
        WebTarget target = client.target(generateURL("/bogus"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals("ResponseFilter was probably not applied to response", HttpResponseCodes.SC_NOT_FOUND * 2, response.getStatus());
        Assert.assertEquals("Wrong body of response", "", entity);
    }

    /**
     * @tpTestDetails Check for injecting ResourceInfo object in end-point
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testAsync() throws Exception {
        WebTarget target = client.target(generateURL("/async"));
        Response response = target.request().post(Entity.entity("hello", "text/plain"));
        String val = response.readEntity(String.class);
        Assert.assertEquals("OK status is expected", HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong body of response", "async", val);
    }
}
