package org.jboss.resteasy.test.contextProxyInterfaces;

import java.util.Collections;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.contextProxyInterfaces.resource.CastableConfigurationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter context injection
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2866
 * @tpSince RESTEasy 3.7
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContextProxyInterfacesTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ContextProxyInterfacesTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        Map<String, String> contextParams = Collections.singletonMap("resteasy.proxy.implement.all.interfaces", "true");
        return TestUtil.finishContainerPrepare(war, contextParams, CastableConfigurationResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextProxyInterfacesTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception {
        client = ResteasyClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testCanCastConfigurationToImplSpecificInterface() throws Exception {
        Builder builder = client.target(generateURL("/config")).request();
        try (Response response = builder.get()) {
            Assert.assertEquals(200, response.getStatus());
            Assert.assertTrue(response.readEntity(String.class).contains("ResteasyProviderFactoryImpl"));
            Assert.assertEquals("true", response.getHeaderString("Instanceof-HeaderValueProcessor"));
        }
    }
}
