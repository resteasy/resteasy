package org.jboss.resteasy.test.context;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.context.resource.ClientThreadContext;
import org.jboss.resteasy.test.context.resource.ClientThreadContextResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that a {@link org.jboss.resteasy.spi.concurrent.ThreadContext} registered on a {@link ClientBuilder} inside a
 * deployment is isolated to the client built from it. A client built afterward does not have it, and it is not added
 * to the deployment's {@link org.jboss.resteasy.spi.ResteasyProviderFactory#getThreadContexts() thread contexts}.
 *
 * @since 8.0
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.6.Final")
public class ClientThreadContextIsolationTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, ClientThreadContextIsolationTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, ClientThreadContextResource.class, ClientThreadContext.class);
    }

    /**
     * The client built from the builder the {@link ClientThreadContext} type was registered on has the context.
     */
    @Test
    public void classRegisteredClient() throws Exception {
        assertContains("class/registered");
    }

    /**
     * A client built after the {@link ClientThreadContext} type was registered on another builder does not have the
     * context.
     */
    @Test
    public void classNewClient() throws Exception {
        assertNotContains("class/new-client");
    }

    /**
     * The deployment does not have the {@link ClientThreadContext} type registered on a client builder.
     */
    @Test
    public void classServer() throws Exception {
        assertNotContains("class/server");
    }

    /**
     * The injected client should not have the {@linkplain ClientThreadContext} type registered.
     */
    @Test
    public void classInjected() throws Exception {
        assertNotContains("class/injected");
    }

    /**
     * The client built from the builder a {@link ClientThreadContext} instance was registered on has the context.
     */
    @Test
    public void instanceRegisteredClient() throws Exception {
        assertContains("instance/registered");
    }

    /**
     * A client built after a {@link ClientThreadContext} instance was registered on another builder does not have
     * the context.
     */
    @Test
    public void instanceNewClient() throws Exception {
        assertNotContains("instance/new-client");
    }

    /**
     * The deployment does not have the {@link ClientThreadContext} instance registered on a client builder.
     */
    @Test
    public void instanceServer() throws Exception {
        assertNotContains("instance/server");
    }

    /**
     * The injected client should not have the {@linkplain ClientThreadContext} instance.
     */
    @Test
    public void instanceInjected() throws Exception {
        assertNotContains("instance/injected");
    }

    private void assertContains(final String path) throws Exception {
        final String contexts = threadContexts(path);
        Assertions.assertTrue(contexts.contains(ClientThreadContext.class.getName()),
                () -> "Expected %s in %s".formatted(ClientThreadContext.class.getName(), contexts));
    }

    private void assertNotContains(final String path) throws Exception {
        final String contexts = threadContexts(path);
        Assertions.assertFalse(contexts.contains(ClientThreadContext.class.getName()),
                () -> "Did not expect %s in %s".formatted(ClientThreadContext.class.getName(), contexts));
    }

    private String threadContexts(final String path) throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client
                        .target(TestUtil.generateUri(baseUri, "client-thread-context/" + path))
                        .request()
                        .get()) {
            final String contexts = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), contexts);
            return contexts;
        }
    }
}
