package org.jboss.resteasy.test.asynch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionBooleanInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionByteInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionCharInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContext;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextAsyncSpecifier;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextErrorSpecifier;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInterface;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInterfaceInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionDoubleInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionException;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionExceptionMapper;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionFloatInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionIntInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionLongInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionPrimitiveInjectorSpecifier;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionResource;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionResource2;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionShortInjector;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Async Request Filter test.
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncInjectionTest {
    protected static final Logger log = Logger.getLogger(AsyncInjectionTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {

        WebArchive war = TestUtil.prepareArchive(AsyncInjectionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncInjectionResource.class,
                AsyncInjectionContext.class, AsyncInjectionContextInjector.class,
                AsyncInjectionContextInterface.class, AsyncInjectionContextInterfaceInjector.class,
                AsyncInjectionContextAsyncSpecifier.class, AsyncInjectionContextErrorSpecifier.class,
                AsyncInjectionException.class, AsyncInjectionExceptionMapper.class,
                AsyncInjectionResource2.class, AsyncInjectionPrimitiveInjectorSpecifier.class,
                AsyncInjectionBooleanInjector.class, AsyncInjectionCharInjector.class,
                AsyncInjectionByteInjector.class, AsyncInjectionShortInjector.class,
                AsyncInjectionIntInjector.class, AsyncInjectionLongInjector.class,
                AsyncInjectionFloatInjector.class, AsyncInjectionDoubleInjector.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncInjectionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Async Injection works
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjection() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection works for interfaces
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionInterface() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/interface"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection does not suspend request if already resolved
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionResolved() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/resolved"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection suspends request if not yet resolved
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionSuspended() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/suspended"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection with exceptions
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionException() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/exception"));

        Response response = base.request()
                .get();
        assertEquals(202, response.getStatus(), "Non-202 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection with async exceptions
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionExceptionAsync() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/exception-async"));

        Response response = base.request()
                .get();
        assertEquals(202, response.getStatus(), "Non-202 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection in places where it does not work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionExceptionLate() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/late"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }

    /**
     * @tpTestDetails Async Injection of primitive types
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testAsyncInjectionPrimitives() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/primitives"));

        Response response = base.request()
                .get();
        assertEquals(200, response.getStatus(), "Non-200 result: " + response.readEntity(String.class));

        client.close();
    }
}
