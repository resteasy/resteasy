package org.jboss.resteasy.test.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.test.client.resource.ClientExecutorShutdownTestResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static org.junit.Assert.fail;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails https://issues.jboss.org/browse/RESTEASY-621
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientExecutorShutdownTest extends ClientTestBase{
    private static Logger log = Logger.getLogger(ClientExecutorShutdownTest.class);

    @Path("/test")
    public interface TestService {
        @POST
        Response post();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientExecutorShutdownTest.class.getSimpleName());
        war.addClass(ClientExecutorShutdownTest.class);
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, ClientExecutorShutdownTestResource.class);
    }

    /**
     * @tpTestDetails Verify that if ApacheHttpClient4Executor creates its own HttpClient,
     * then ApacheHttpClient4Executor.finalize() will close the HttpClient's
     * org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.finalize() will close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorNonSharedHttpClientFinalize() throws Throwable {
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine();
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        engine.finalize();
        HttpClient httpClient = engine.getHttpClient();
        HttpPost post = new HttpPost(generateURL("/test"));
        try {
            httpClient.execute(post);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            log.info("Got expected IllegalStateException");
        }
    }

    /**
     * @tpTestDetails Verify that if ApacheHttpClient4Executor creates its own HttpClient,
     * then ApacheHttpClient4Executor.close() will close the HttpClient's
     * org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.close() will close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorNonSharedHttpClientClose() throws Throwable {
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine();
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        engine.close();
        HttpClient httpClient = engine.getHttpClient();
        HttpPost post = new HttpPost(generateURL("/test"));
        try {
            httpClient.execute(post);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            log.info("Got expected IllegalStateException");
        }
    }

    /**
     * @tpTestDetails Verify that if ApacheHttpClient4Executor receives an HttpClient through
     * a constructor, then ApacheHttpClient4Executor.finalize() will not close the
     * HttpClient's org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.finalize() will not close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorSharedHttpClientFinalize() throws Throwable {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        engine.finalize();
        Assert.assertEquals("Original httpclient and engine httpclient are not the same instance",
                httpClient, engine.getHttpClient());
        HttpPost post = new HttpPost(generateURL("/test"));
        HttpResponse httpResponse = httpClient.execute(post);
        Assert.assertEquals("The httpclient was closed and it shouldn't", HttpResponseCodes.SC_NO_CONTENT,
                httpResponse.getStatusLine().getStatusCode());
    }

    /**
     * @tpTestDetails Verify that if ApacheHttpClient4Executor receives an HttpClient through
     * a constructor, then ApacheHttpClient4Executor.close() will not close the
     * HttpClient's org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.close() will not close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorSharedHttpClientClose() throws Throwable {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assert.assertEquals("Original httpclient and engine httpclient are not the same instance",
                HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        engine.close();
        Assert.assertEquals(httpClient, engine.getHttpClient());
        HttpPost post = new HttpPost(generateURL("/test"));
        HttpResponse httpResponse = httpClient.execute(post);
        Assert.assertEquals("The httpclient was closed and it shouldn't", HttpResponseCodes.SC_NO_CONTENT,
                httpResponse.getStatusLine().getStatusCode());
    }
}
