package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorHello;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorHelloResource;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorInjectorFactoryImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for custom value injector.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomValueInjectorTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CustomValueInjectorTest.class.getSimpleName());
        war.addClass(CustomValueInjectorHello.class);
        return TestUtil.finishContainerPrepare(war, null, CustomValueInjectorHelloResource.class,
                CustomValueInjectorInjectorFactoryImpl.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomValueInjectorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomInjectorFactory() throws Exception {
        String result = client.target(generateURL("/")).request().get(String.class);
        Assert.assertEquals("Response has wrong content", "world", result);
    }

}
