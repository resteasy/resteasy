package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.basic.resource.ApplicationPropertiesConfigPropertyApplicationInjection;
import org.jboss.resteasy.test.core.basic.resource.ApplicationPropertiesConfigPropertyApplicationInjectionFeature;
import org.jboss.resteasy.test.core.basic.resource.ApplicationPropertiesConfigPropertyApplicationInjectionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for custom Application class with overridden getProperties() method
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ApplicationPropertiesConfigPropertyApplicationInjectionTest {
    static Client client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,
                ApplicationPropertiesConfigPropertyApplicationInjectionTest.class.getSimpleName() + ".war");
        war.addClasses(ApplicationPropertiesConfigPropertyApplicationInjection.class,
                ApplicationPropertiesConfigPropertyApplicationInjectionResource.class,
                ApplicationPropertiesConfigPropertyApplicationInjectionFeature.class);
        return war;
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path,
                ApplicationPropertiesConfigPropertyApplicationInjectionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for custom Application class with overriden getProperties() method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApplicationPropertiesConfigApplicationInjection() {
        WebTarget target = client.target(generateURL("/getconfigproperty"));
        String response = target.queryParam("prop", "Prop1").request().get(String.class);
        Assertions.assertEquals("Value1", response, "The property is not found in the deployment");
    }
}
