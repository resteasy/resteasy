package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderObject;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderResource;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderApplication;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for anonymous classes as resource added to REST singletons
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SingletonCustomProviderTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, SingletonCustomProviderTest.class.getSimpleName() + ".war");
        war.addClasses(SingletonCustomProviderApplication.class, SingletonCustomProviderObject.class,
                SingletonCustomProviderResource.class);
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SingletonCustomProviderTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check post request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMessageReaderThrowingWebApplicationException() throws Exception {
        Response response = client.target(generateURL("/test")).request().post(Entity.entity("foo", "application/octet-stream"));
        Assert.assertEquals("Wrong response status", 999, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check get request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMessageWriterThrowingWebApplicationException() throws Exception {
        Response response = client.target(generateURL("/test")).request().get();
        Assert.assertEquals("Wrong response status", 999, response.getStatus());
        response.close();
    }
}
