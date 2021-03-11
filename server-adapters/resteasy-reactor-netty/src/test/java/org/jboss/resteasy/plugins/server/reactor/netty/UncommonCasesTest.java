package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UncommonCasesTest {

    private static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        final ResteasyDeployment deployment = ReactorNettyContainer.start();
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() {
        client.close();
        ReactorNettyContainer.stop();
    }

    /**
     * Verifies that no errors occurs in the event one or multiple requests are sent with empty body.
     */
    @Test
    public void testEmptyBody() {

        final WebTarget target = client.target(generateURL("/basic"));
        final Response resp = target.request().post(null);
        assertEquals(200, resp.getStatus());
        assertEquals("POST ", resp.readEntity(String.class));

        final Response resp2 = target.request().put(null);
        assertEquals(200, resp2.getStatus());
        assertEquals("PUT ", resp2.readEntity(String.class));

    }

}