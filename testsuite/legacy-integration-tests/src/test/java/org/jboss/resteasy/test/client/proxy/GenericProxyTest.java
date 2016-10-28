package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxyBase;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxySpecificProxy;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxyResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1047.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GenericProxyTest {
    private static ResteasyClient client;

    @BeforeClass
    public static void before() throws Exception {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GenericProxyTest.class.getSimpleName());
        war.addClasses(GenericProxyBase.class, GenericProxySpecificProxy.class);
        return TestUtil.finishContainerPrepare(war, null, GenericProxyResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(GenericProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test generic proxy in client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEcho() {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        GenericProxySpecificProxy proxy = target.proxy(GenericProxySpecificProxy.class);
        String hello = proxy.sayHi("hello");
        Assert.assertEquals("Response has wrong content", "hello", hello);
        hello = proxy.sayHi("hello123");
        Assert.assertEquals("Response has wrong content", "hello123", hello);
    }

    /**
     * @tpTestDetails Test generic proxy in client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEchoNoProxy() {
        ResteasyWebTarget target = client.target(generateBaseUrl() + "/say/hello");
        Response response = target.request().post(Entity.text("hello"));

        String hello = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Response has wrong content", "hello", hello);

        response.close();
    }
}
