package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.resource.PrimitiveResource;
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
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PrimitiveTest extends ClientTestBase {

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PrimitiveTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PrimitiveResource.class);
    }

    /**
     * @tpTestDetails Client sends POST request with text entity, server sends echoes to value and returns int.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInt() {
        Response response = client.target(generateURL("/int")).request().post(Entity.text("5"));
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("5", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with text entity, server sends echoes to value and returns boolean.
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBoolean() {
        Response response = client.target(generateURL("/boolean")).request().post(Entity.text("true"));
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("true", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request, server sends Accepted response
     * @tpPassCrit Correct response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBuildResponse() {
        Response response = client.target(generateURL("/nothing")).request().get();
        Assertions.assertEquals("", response.readEntity(String.class));
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }
}
