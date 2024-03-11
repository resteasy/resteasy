package org.jboss.resteasy.test.client.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.DefaultMediaTypesResource;
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
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class DefaultMediaTypesTest {

    public interface Foo {
        @GET
        @Path("foo")
        String getFoo();

        @PUT
        @Path("foo")
        String setFoo(String value);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DefaultMediaTypesTest.class.getSimpleName());
        war.addClass(DefaultMediaTypesTest.class);
        return TestUtil.finishContainerPrepare(war, null, DefaultMediaTypesResource.class);
    }

    static ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DefaultMediaTypesTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends request thru client proxy, no default consumes type is specified.
     * @tpPassCrit Runtime exception is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void testOldBehaviorContinues() throws Exception {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    ResteasyWebTarget target = client.target(generateURL("/foo"));
                    target.proxy(Foo.class);
                });
        Assertions.assertTrue(thrown instanceof RuntimeException);
    }

    /**
     * @tpTestDetails Client sends request thru client proxy, the request has specified default produces and consumes type
     * @tpPassCrit The response contains acceptable media types set up by client (text/plain)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDefaultValues() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/foo"));
        Foo foo = target.proxyBuilder(Foo.class).defaultProduces(MediaType.TEXT_PLAIN_TYPE)
                .defaultConsumes(MediaType.TEXT_PLAIN_TYPE).build();

        assertEquals("[text/plain]", foo.getFoo(),
                "The reponse header doesn't contain the expected media type");
        assertEquals("text/plain", foo.setFoo("SOMETHING"),
                "The reponse header doesn't contain the expected media type");
    }

    /**
     * @tpTestDetails Client sends request thru client proxy, the request has specified default produces and consumes type
     * @tpPassCrit The response contains acceptable media types set up by client (application/json)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMismatch() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/foo"));
        Foo foo = target.proxyBuilder(Foo.class).defaultProduces(MediaType.APPLICATION_JSON_TYPE)
                .defaultConsumes(MediaType.APPLICATION_JSON_TYPE).build();

        assertEquals("[application/json]", foo.getFoo(),
                "The reponse header doesn't contain the expected media type");
        assertEquals("application/json", foo.setFoo("SOMETHING"),
                "The reponse header doesn't contain the expected media type");
    }
}
