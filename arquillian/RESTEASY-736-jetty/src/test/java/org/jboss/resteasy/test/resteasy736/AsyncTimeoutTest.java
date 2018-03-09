package org.jboss.resteasy.test.resteasy736;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy736.TestApplication;
import org.jboss.resteasy.resteasy736.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *          <p>
 *          Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncTimeoutTest {
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
//        System.out.println("url = " + url);
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + "test/").request();
        long start = System.currentTimeMillis();
//        System.out.println("start:   " + start);
        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            System.out.println("finish:  " + System.currentTimeMillis());
            long elapsed = System.currentTimeMillis() - start;
//            System.out.println("elapsed: " + elapsed + " ms");
            ;
//            System.out.println("status: " + response.getStatus());
            assertTrue(response != null);
//            System.out.println("response: " + response.readEntity(String.class));
            Assert.assertEquals("Status is wrong", 503, response.getStatus());
            assertTrue(elapsed < 10000);
        }
    }

    @Ignore
    @Test
    public void testDefaultAsynchTimeout() throws Exception {
        Builder request = ResteasyClientBuilder.newClient().target(url.toString() + "default/").request();
        long start = System.currentTimeMillis();
        System.out.println("start:   " + start);
        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("finish:  " + System.currentTimeMillis());
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("elapsed: " + elapsed + " ms");
            System.out.println("status: " + response.getStatus());
            assertTrue(response != null);
            System.out.println("response: " + response.readEntity(String.class));
            Assert.assertEquals("Wrong response", 503, response.getStatus());
            Assert.assertTrue("Should timeout", elapsed < 36000); // Jetty async timeout defaults to 30000.
        }
    }
}
