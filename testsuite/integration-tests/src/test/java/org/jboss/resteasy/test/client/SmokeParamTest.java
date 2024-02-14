package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.resource.SmokeParamResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke parameter test.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SmokeParamTest extends ClientTestBase {

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SmokeParamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SmokeParamResource.class);
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test one request with header and query parameter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSimple() throws Exception {
        Response response = client.target(generateURL("/foo")).request()
                .post(Entity.entity("hello world", "text/plain"));
        Assertions.assertEquals("hello world", response.readEntity(String.class));
        response.close();

        response = client.target(generateURL("/foo")).queryParam("b", "world").request()
                .header("a", "hello")
                .get();
        Assertions.assertEquals("hello world", response.readEntity(String.class));
        response.close();

    }
}
