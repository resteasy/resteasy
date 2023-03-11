package org.jboss.resteasy.test.client.proxy;

import static org.junit.Assert.assertEquals;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.DefaultMediaTypesResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
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
    @Test(expected = RuntimeException.class)
    public void testOldBehaviorContinues() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/foo"));
        target.proxy(Foo.class);
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

        assertEquals("The reponse header doesn't contain the expected media type", "[text/plain]", foo.getFoo());
        assertEquals("The reponse header doesn't contain the expected media type", "text/plain", foo.setFoo("SOMETHING"));
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

        assertEquals("The reponse header doesn't contain the expected media type", "[application/json]", foo.getFoo());
        assertEquals("The reponse header doesn't contain the expected media type", "application/json", foo.setFoo("SOMETHING"));
    }
}
