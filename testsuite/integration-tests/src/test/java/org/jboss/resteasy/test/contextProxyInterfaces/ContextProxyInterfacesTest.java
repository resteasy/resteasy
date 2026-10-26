package org.jboss.resteasy.test.contextProxyInterfaces;

import java.util.Collections;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.contextProxyInterfaces.resource.CastableConfigurationResource;
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
 * @tpSubChapter context injection
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2866
 * @tpSince RESTEasy 3.7
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeAll
    public static void before() throws Exception {
        client = ResteasyClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testCanCastConfigurationToImplSpecificInterface() throws Exception {
        Builder builder = client.target(generateURL("/config")).request();
        try (Response response = builder.get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertTrue(response.readEntity(String.class).contains("ResteasyProviderFactoryImpl"));
            // Before required CDI integration, the injected Configuration implemented all interfaces. With CDI it's
            // not possible to do this as we don't have the instance object when we create the proxy. This value should
            // now be false. We could skip the check, but it seems okay to keep in case something in the future changes.
            Assertions.assertEquals("false", response.getHeaderString("Instanceof-HeaderValueProcessor"));
        }
    }
}
