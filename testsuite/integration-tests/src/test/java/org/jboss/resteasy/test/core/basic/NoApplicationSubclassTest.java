package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.NoApplicationSubclassResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for discovering root resource classes when no Application subclass is present
 * @tpSince RESTEasy 3.6.2.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NoApplicationSubclassTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, NoApplicationSubclassTest.class.getSimpleName() + ".war");
        war.addClasses(NoApplicationSubclassResource.class);
        war.addAsWebInfResource(NoApplicationSubclassTest.class.getPackage(), "NoApplicationSubclassWeb.xml", "web.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NoApplicationSubclassTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check if resource is present in application
     * @tpSince RESTEasy 3.6.2.Final
     */
    @Test
    public void testResource() {
        Response response = client.target(generateURL("/myresources/hello")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello world", response.readEntity(String.class),
                "Wrong content of response");
        response.close();
    }
}
