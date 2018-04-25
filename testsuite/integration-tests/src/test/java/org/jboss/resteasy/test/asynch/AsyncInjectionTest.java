package org.jboss.resteasy.test.asynch;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.UndertowTestRunner;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContext;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextAsyncSpecifier;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInterface;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionContextInterfaceInjector;
import org.jboss.resteasy.test.asynch.resource.AsyncInjectionResource;
import org.jboss.resteasy.test.asynch.resource.AsyncPreMatchRequestFilter1;
import org.jboss.resteasy.test.asynch.resource.AsyncPreMatchRequestFilter2;
import org.jboss.resteasy.test.asynch.resource.AsyncPreMatchRequestFilter3;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter1;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter2;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter3;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilterResource;
import org.jboss.resteasy.test.asynch.resource.AsyncResponseFilter;
import org.jboss.resteasy.test.asynch.resource.AsyncResponseFilter1;
import org.jboss.resteasy.test.asynch.resource.AsyncResponseFilter2;
import org.jboss.resteasy.test.asynch.resource.AsyncResponseFilter3;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Async Request Filter test.
 * @tpSince RESTEasy 4.0.0
 */
//@RunWith(Arquillian.class)
@RunWith(UndertowTestRunner.class)
@RunAsClient
public class AsyncInjectionTest {
    protected static final Logger log = LogManager.getLogger(AsyncInjectionTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {

        WebArchive war = TestUtil.prepareArchive(AsyncInjectionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncInjectionResource.class, 
              AsyncInjectionContext.class, AsyncInjectionContextInjector.class,
              AsyncInjectionContextInterface.class, AsyncInjectionContextInterfaceInjector.class,
              AsyncInjectionContextAsyncSpecifier.class);
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
        assertEquals("Non-200 result: "+response.readEntity(String.class), 200, response.getStatus());
        
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
        assertEquals("Non-200 result: "+response.readEntity(String.class), 200, response.getStatus());
        
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
        assertEquals("Non-200 result: "+response.readEntity(String.class), 200, response.getStatus());
        
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
        assertEquals("Non-200 result: "+response.readEntity(String.class), 200, response.getStatus());
        
        client.close();
    }
}
