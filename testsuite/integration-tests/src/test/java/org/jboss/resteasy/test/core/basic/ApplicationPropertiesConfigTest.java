package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.basic.resource.ApplicationPropertiesConfig;
import org.jboss.resteasy.test.core.basic.resource.ApplicationPropertiesConfigResource;
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
 * @tpTestCaseDetails Test for custom Application class with overriden getProperties() method, by injecting Configuration into
 *                    the resource.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ApplicationPropertiesConfigTest {
    static Client client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, ApplicationPropertiesConfigTest.class.getSimpleName() + ".war");
        war.addClasses(ApplicationPropertiesConfig.class, ApplicationPropertiesConfigResource.class);
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
        return PortProviderUtil.generateURL(path, ApplicationPropertiesConfigTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for custom Application class with overriden getProperties() method, by injecting Configuration
     *                into the resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApplicationPropertiesConfig() {
        String errorMessage = "The property is not found in the deployment";
        String response;
        try {
            WebTarget target = client.target(generateURL("/getconfigproperty"));
            response = target.queryParam("prop", "Prop1").request().get(String.class);
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
        Assertions.assertEquals("Value1", response, errorMessage);
    }
}
