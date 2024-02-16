package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorHello;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorHelloResource;
import org.jboss.resteasy.test.providers.custom.resource.CustomValueInjectorInjectorFactoryImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for custom value injector.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        Assertions.assertEquals("world", result, "Response has wrong content");
    }

}
