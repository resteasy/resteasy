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
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter1;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter2;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilter3;
import org.jboss.resteasy.test.asynch.resource.AsyncRequestFilterResource;
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
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncRequestFilterTest {
    protected static final Logger log = LogManager.getLogger(AsyncRequestFilterTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {

        WebArchive war = TestUtil.prepareArchive(AsyncRequestFilterTest.class.getSimpleName());
        war.addClasses(AsyncRequestFilterResource.class, AsyncRequestFilter.class, AsyncRequestFilter1.class, AsyncRequestFilter2.class, AsyncRequestFilter3.class);
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncRequestFilterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Interceptors work
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testRequestFilters() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/"));

        // all sync
        
        Response response = base.request()
           .header("Filter1", "sync-pass")
           .header("Filter2", "sync-pass")
           .header("Filter3", "sync-pass")
           .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "sync-fail")
              .header("Filter2", "sync-fail")
              .header("Filter3", "sync-fail")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter1", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "sync-pass")
              .header("Filter2", "sync-fail")
              .header("Filter3", "sync-fail")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter2", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "sync-pass")
              .header("Filter2", "sync-pass")
              .header("Filter3", "sync-fail")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter3", response.readEntity(String.class));

        // async
        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "sync-pass")
              .header("Filter3", "sync-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "async-pass")
              .header("Filter3", "sync-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "async-pass")
              .header("Filter3", "async-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "sync-pass")
              .header("Filter3", "async-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "sync-pass")
              .header("Filter2", "async-pass")
              .header("Filter3", "sync-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("resource", response.readEntity(String.class));

        // async failures

        response = base.request()
              .header("Filter1", "async-fail")
              .header("Filter2", "sync-fail")
              .header("Filter3", "sync-fail")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter1", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "sync-fail")
              .header("Filter3", "sync-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter2", response.readEntity(String.class));

        response = base.request()
              .header("Filter1", "async-pass")
              .header("Filter2", "async-fail")
              .header("Filter3", "sync-pass")
              .get();
        assertEquals(200, response.getStatus());
        assertEquals("Filter2", response.readEntity(String.class));

        client.close();
    }
}
