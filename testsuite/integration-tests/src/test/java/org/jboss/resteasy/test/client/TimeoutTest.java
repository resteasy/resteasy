package org.jboss.resteasy.test.client;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.resource.TimeoutResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TimeoutTest extends ClientTestBase {
    @Path("/timeout")
    public interface TimeoutResourceInterface {
        @GET
        @Produces("text/plain")
        String get(@QueryParam("sleep") int sleep) throws Exception;
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TimeoutTest.class.getSimpleName());
        war.addClass(TimeoutTest.class);
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, TimeoutResource.class);
    }

    /**
     * @tpTestDetails Create client with custom SocketTimeout setting. Client sends GET request for the resource which
     *                calls sleep() for the specified amount of time.
     * @tpPassCrit The request gets timeouted
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        ResteasyClient clientengine = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).readTimeout(2, TimeUnit.SECONDS)
                .build();
        ClientHttpEngine engine = clientengine.httpEngine();
        Assert.assertNotNull("Client engine is was not created", engine);

        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        ResteasyWebTarget target = client.target(generateURL("/timeout"));
        try {
            target.queryParam("sleep", "5").request().get();
            Assert.fail("The request didn't timeout as expected");
        } catch (ProcessingException e) {
            Assert.assertTrue(String.format("Expected %s or %s found %s", SocketTimeoutException.class.getName(),
                    HttpTimeoutException.class.getName(), e.getCause()),
                    e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof HttpTimeoutException);
        }

        TimeoutResourceInterface proxy = client.target(generateURL("")).proxy(TimeoutResourceInterface.class);
        try {
            proxy.get(5);
            Assert.fail("The request didn't timeout as expected when using client proxy");
        } catch (ProcessingException e) {
            Assert.assertTrue(String.format("Expected %s or %s found %s", SocketTimeoutException.class.getName(),
                    HttpTimeoutException.class.getName(), e.getCause()),
                    e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof HttpTimeoutException);
        }
        clientengine.close();
    }
}
