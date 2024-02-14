package org.jboss.resteasy.test.client;

import static org.junit.jupiter.api.Assertions.fail;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.ClientExecutorShutdownTestResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails https://issues.jboss.org/browse/RESTEASY-621
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientExecutorShutdownTest extends ClientTestBase {
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
     *                then ApacheHttpClient4Executor.finalize() will close the HttpClient's
     *                org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.finalize() will close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorNonSharedHttpClientFinalize() throws Throwable {
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
     * @tpTestDetails Verify that if ApacheHttpClient4Executor creates its own HttpClient,
     *                then ApacheHttpClient4Executor.close() will close the HttpClient's
     *                org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.close() will close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorNonSharedHttpClientClose() throws Throwable {
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
     *                a constructor, then ApacheHttpClient4Executor.finalize() will not close the
     *                HttpClient's org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.finalize() will not close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorSharedHttpClientFinalize() throws Throwable {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient, false);
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        engine.close();
        Assertions.assertEquals(httpClient, engine.getHttpClient(),
                "Original httpclient and engine httpclient are not the same instance");
        HttpPost post = new HttpPost(generateURL("/test"));
        HttpResponse httpResponse = httpClient.execute(post);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT,
                httpResponse.getStatusLine().getStatusCode(), "The httpclient was closed and it shouldn't");
    }

    /**
     * @tpTestDetails Verify that if ApacheHttpClient4Executor receives an HttpClient through
     *                a constructor, then ApacheHttpClient4Executor.close() will not close the
     *                HttpClient's org.apache.http.conn.ClientConnectionManager.
     * @tpPassCrit ApacheHttpClient4Executor.close() will not close the connection
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testApacheHttpClient4ExecutorSharedHttpClientClose() throws Throwable {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient, false);
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = client.target(generateURL("/test")).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus(),
                "Original httpclient and engine httpclient are not the same instance");
        engine.close();
        Assertions.assertEquals(httpClient, engine.getHttpClient());
        HttpPost post = new HttpPost(generateURL("/test"));
        HttpResponse httpResponse = httpClient.execute(post);
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode(),
                "The httpclient was closed and it shouldn't");
    }
}
