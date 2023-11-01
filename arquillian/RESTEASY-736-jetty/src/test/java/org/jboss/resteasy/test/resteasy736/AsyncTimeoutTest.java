package org.jboss.resteasy.test.resteasy736;

import java.net.URI;

import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy736.TestApplication;
import org.jboss.resteasy.resteasy736.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *          <p>
 *          Copyright Aug 3, 2012
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncTimeoutTest {

    private static final Logger LOG = Logger.getLogger(AsyncTimeoutTest.class);

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-736.war")
                .addClasses(TestApplication.class, TestResource.class)
                .addClasses(AsyncTimeoutTest.class)
                .addAsWebInfResource("web.xml");
        return war;
    }

    @ArquillianResource
    URI url;

    @Test
    public void testAsynchTimeout() throws Exception {
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + "test/").request();
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            Assertions.assertNotNull(response);
            Assertions.assertEquals(503, response.getStatus(), "Status is wrong");
            Assertions.assertTrue(elapsed < 10000);
        }
    }

    @Disabled
    @Test
    public void testDefaultAsynchTimeout() throws Exception {
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + "default/").request();
        long start = System.currentTimeMillis();
        LOG.info("start:   " + start);
        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            LOG.error("Error: ", e);
        } finally {
            Assertions.assertNotNull(response);
            LOG.info("finish:  " + System.currentTimeMillis());
            long elapsed = System.currentTimeMillis() - start;
            LOG.info("elapsed: " + elapsed + " ms");
            LOG.info("status: " + response.getStatus());
            LOG.info("response: " + response.readEntity(String.class));
            Assertions.assertEquals(503, response.getStatus(), "Wrong response");
            Assertions.assertTrue(elapsed < 36000, "Should timeout"); // Jetty async timeout defaults to 30000.
        }
    }
}
