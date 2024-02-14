package org.jboss.resteasy.test.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClientEngine;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.AsyncTimeoutExceptionsResource;
import org.jboss.resteasy.test.client.resource.AsyncTimeoutExceptionsSticker;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests client exception handling for AsyncInvoker interface and InvocationCallBack interface.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncTimeoutExceptionsTest extends ClientTestBase {

    protected static final Logger logger = Logger.getLogger(AsyncTimeoutExceptionsTest.class.getName());

    public Client client;

    @BeforeEach
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AsyncTimeoutExceptionsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncTimeoutExceptionsResource.class,
                AsyncTimeoutExceptionsSticker.class, StickerCallback.class, ResponseCallback.class);
    }

    @AfterEach
    public void close() {
        client.close();
    }

    public static class StickerCallback implements InvocationCallback<AsyncTimeoutExceptionsSticker> {

        @Override
        public void completed(AsyncTimeoutExceptionsSticker sticker) {
            logger.info(sticker.getName());
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                logger.info(throwable.toString());
            } else {
                logger.error("Sleep was interrupted", throwable);
            }
        }
    }

    public static class ResponseCallback implements InvocationCallback<Response> {

        @Override
        public void completed(Response response) {
            logger.info("OK");
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                logger.info(throwable.toString());
            } else {
                logger.error("Sleep was interrupted", throwable);
            }
        }
    }

    /*
     * Instantiates Apache httpclient to handle multiple connections
     */
    private Client prepareHttpClientForMultipleRequests() {

        RequestConfig reqConfig = RequestConfig.custom() // apache HttpClient specific
                .setConnectTimeout(2000)
                .setSocketTimeout(-1)
                .setConnectionRequestTimeout(200)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(reqConfig)
                .build();
        return ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(ApacheHttpClientEngine.create(httpClient, true))
                .build(); // RESTEasy specific
    }

    /**
     * @tpTestDetails Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutExcetion.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void futureTimeOutSleepTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async()
                            .get(AsyncTimeoutExceptionsSticker.class);
                    AsyncTimeoutExceptionsSticker stickerName = future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     *                Asynchronous processing is invoked on the server - the current thread on the server is detached, but it is
     *                not
     *                run, resulting to client Throws TimeoutException.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void futureAsyncOnServerAndTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker2"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async()
                            .get(AsyncTimeoutExceptionsSticker.class);
                    AsyncTimeoutExceptionsSticker stickerName = future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     *                Asynchronous processing is invoked on the server - the current thread on the server is detached and
     *                request is processed
     *                asynchronously on the server and processing thread is suspended.
     *                Client is expected to throw TimeoutException.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void futureAsyncOnServerClientTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker3"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async()
                            .get(AsyncTimeoutExceptionsSticker.class);
                    AsyncTimeoutExceptionsSticker stickerName = future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    //=============================================================================================================

    /**
     * @tpTestDetails Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     *                The resource is supposed to return Response object.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void futureTimeOutWithResponseTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/get"));
                    Future<Response> future = base.request().async().get();
                    Response response = future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     *                Another asynchronous request is invoked and it is asserted that the same client will handle it
     *                successfully.
     * @tpInfo Server throws RejectedExecutionException in the end, see WFCORE-756 and "UT015005: Error invoking method
     *         requestDestroyed" - WFLY-2837
     * @tpPassCrit Client handles successfully asynchronous request after exception is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void futureTimeoutAndMoreRequestsTest() throws InterruptedException, ExecutionException, TimeoutException {

        final int multiple = 6;

        Client apacheClient = prepareHttpClientForMultipleRequests();
        WebTarget base = apacheClient.target(generateURL("/get"));
        Future<Response> future = base.request().async().get();
        Response response = null;
        try {
            response = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            Assertions.assertEquals(TimeoutException.class.getName(), ex.toString());
        }

        for (int i = 0; i < multiple; i++) {
            WebTarget baseMultiple = apacheClient.target(generateURL("/getPositive"));
            future = baseMultiple.request().async().get();
            response = future.get(5, TimeUnit.SECONDS);
            response.close();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        }
        apacheClient.close();
    }

    //=============================================================================================================
    // Invocation callbacks
    //=============================================================================================================

    /**
     * @tpTestDetails Invocation callback should close all connections by itself
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutExcetion.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void invocationCallbackTimeoutSleepTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async().get(new StickerCallback());
                    future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Invocation callback should close all connections by itself
     *                Asynchronous processing is invoked on the server - the current thread on the server is detached, but it is
     *                not
     *                run, resulting to client Throws TimeoutException.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void invocationCallbackAsyncOnServerAndTimeoutTest()
            throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker2"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async().get(new StickerCallback());
                    future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Invocation callback should close all connections by itself
     *                Asynchronous processing is invoked on the server - the current thread on the server is detached and
     *                request is processed
     *                asynchronously on the server and processing thread is suspended.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void invocationCallbackAsyncOnServerClientTimeoutTest()
            throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/sticker3"));
                    Future<AsyncTimeoutExceptionsSticker> future = base.request().async().get(new StickerCallback());
                    future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    //=============================================================================================================

    /**
     * @tpTestDetails Invocation callback should close all connections by itself
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     *                The resource is supposed to return Response object.
     * @tpPassCrit TimeoutException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void invocationCallbackTimeoutWithResponseTest() throws InterruptedException, ExecutionException, TimeoutException {
        TimeoutException thrown = Assertions.assertThrows(TimeoutException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/get"));
                    Future<Response> future = base.request().async().get(new ResponseCallback());
                    future.get(5, TimeUnit.SECONDS);
                });
        Assertions.assertTrue(thrown instanceof TimeoutException);
    }

    /**
     * @tpTestDetails Invocation callback should close all connections by itself.
     *                Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     *                Another asynchronous request is invoked and it is asserted that the same client will handle it
     *                successfully.
     * @tpInfo Server throws RejectedExecutionException in the end, see WFCORE-756 and "UT015005: Error invoking method
     *         requestDestroyed" - WFLY-2837
     * @tpPassCrit Client handles successfully asynchronous request after exception is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void invocationCallbackTimeoutAndMoreRequestsTest()
            throws InterruptedException, ExecutionException, TimeoutException {

        final int multiple = 6;

        Client apacheClient = prepareHttpClientForMultipleRequests();
        WebTarget base = apacheClient.target(generateURL("/get"));
        Future<Response> future = base.request().async().get(new ResponseCallback());
        Response response = null;
        try {
            response = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            Assertions.assertEquals(TimeoutException.class.getName(), ex.toString());
        }

        for (int i = 0; i < multiple; i++) {
            WebTarget baseMultiple = apacheClient.target(generateURL("/getPositive"));
            future = baseMultiple.request().async().get(new ResponseCallback());
            response = future.get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        }
        apacheClient.close();
    }

}
