package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.proxy.resource.ContextTestResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ContextTest {

    @Path(value = "/test")
    public interface ResourceInterface {

        @GET
        @Produces("text/plain")
        String echo(@Context UriInfo info);
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ContextTest.class.getSimpleName());
        war.addClass(ContextTest.class);
        return TestUtil.finishContainerPrepare(war, null, ContextTestResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends async GET requests thru client proxy. UriInfo is injected as argument of the GET
     *                method call.
     * @tpPassCrit UriInfo was injected into method call
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContextInjectionProxy() {
        ResourceInterface proxy = client.target(generateURL("")).proxy(ResourceInterface.class);
        Assertions.assertEquals("content", proxy.echo(null), "UriInfo was not injected");
    }
}
