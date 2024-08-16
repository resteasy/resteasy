package org.jboss.resteasy.plugins.server.reactor.netty;

import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.util.SSLCerts;
import org.jboss.resteasy.util.PortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import io.netty.handler.ssl.ClientAuth;

/**
 * Tests to make sure clean up tasks are executed after request is serviced.
 * A countdown latch is used to countdown each call is accounted for.
 */
public class CleanUpTasksTest {

    /**
     * JAX-RS Client
     */
    private static Client client;
    /**
     * Count down latch to count clean up tasks invocation
     */
    private static final CountDownLatch TEST_LATCH = new CountDownLatch(1);

    @BeforeAll
    public static void setup() {
        Runnable runnable = () -> TEST_LATCH.countDown();
        final SSLContext clientContext = SSLCerts.DEFAULT_TRUSTSTORE.getSslContext();
        final SSLContext serverContext = SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext();

        final ReactorNettyJaxrsServer reactorNettyJaxrsServer = new ReactorNettyJaxrsServer();
        reactorNettyJaxrsServer.setPort(PortProvider.getPort());
        reactorNettyJaxrsServer.setRootResourcePath("");
        reactorNettyJaxrsServer.setSecurityDomain(null);
        reactorNettyJaxrsServer.setSSLContext(serverContext);
        reactorNettyJaxrsServer.setClientAuth(ClientAuth.OPTIONAL);
        reactorNettyJaxrsServer.setCleanUpTasks(Collections.singletonList(runnable));

        final ResteasyDeployment deployment = ReactorNettyContainer.start(reactorNettyJaxrsServer);
        deployment.getProviderFactory().registerProvider(JacksonJsonProvider.class);

        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(BasicResource.class);

        client = ClientBuilder
                .newBuilder()
                .sslContext(clientContext)
                .build()
                .register(JacksonJsonProvider.class);
    }

    /**
     * Tests to verify clean up tasks are run. Count of latch always needs to be 0
     */
    @Test
    public void testCleanUpTasks() {
        WebTarget target = client.target(String.format("https://%s:%d%s", getHost(), getPort(), "/basic"));
        String val = target.request().get(String.class);
        Assertions.assertEquals("Hello world!", val);
        Assertions.assertEquals(0, TEST_LATCH.getCount());
    }

    @AfterAll
    public static void end() {
        Assertions.assertEquals(0, TEST_LATCH.getCount());
        client.close();
        ReactorNettyContainer.stop();
    }
}
