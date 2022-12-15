package org.jboss.resteasy.test.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.rxjava2.FlowableRxInvoker;
import org.jboss.resteasy.test.response.resource.AsyncResponseCallback;
import org.jboss.resteasy.test.response.resource.AsyncResponseException;
import org.jboss.resteasy.test.response.resource.AsyncResponseExceptionMapper;
import org.jboss.resteasy.test.response.resource.PublisherResponseResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

/**
 * @tpSubChapter Publisher response type
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PublisherResponseTest {

    Client client;

    private static final Logger logger = Logger.getLogger(PublisherResponseTest.class);
    private static CountDownLatch latch;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PublisherResponseTest.class.getSimpleName());
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.reactivestreams\n"));
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("modifyThread")), "permissions.xml");

        return TestUtil.finishContainerPrepare(war, null, PublisherResponseResource.class,
                AsyncResponseCallback.class, AsyncResponseExceptionMapper.class, AsyncResponseException.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PublisherResponseTest.class.getSimpleName());
    }

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
        latch = new CountDownLatch(1);
    }

    @After
    public void close() {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>.
     * @tpSince RESTEasy 4.0
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testText() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/text")).request().rx(FlowableRxInvoker.class);
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        ArrayList<String> list = new ArrayList<String>();
        flowable.subscribe(
                (String s) -> list.add(s),
                (Throwable t) -> logger.error("Error:", t),
                () -> latch.countDown());
        latch.await();
        Assert.assertEquals(Arrays.asList(new String[] { "one", "two" }), list);

        // make sure the completion callback was called with no error
        Builder request = client.target(generateURL("/callback-called-no-error/text")).request();
        Response response = request.get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>, throws exception immediately.
     * @tpSince RESTEasy 4.0
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTextErrorImmediate() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/text-error-immediate")).request().rx(FlowableRxInvoker.class);
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        AtomicReference<Object> value = new AtomicReference<Object>();
        flowable.subscribe(
                (String s) -> {
                },
                (Throwable t) -> {
                    value.set(t);
                    latch.countDown();
                },
                () -> {
                });
        latch.await();
        ClientErrorException cee = (ClientErrorException) value.get();
        Assert.assertEquals("Got it", cee.getResponse().readEntity(String.class));

        // make sure the completion callback was called with with an error
        Builder request = client.target(generateURL("/callback-called-with-error/text-error-immediate")).request();
        Response response = request.get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>, throws exception in stream.
     * @tpSince RESTEasy 4.0
     */
    @Test
    @Ignore // Doesn't currently work. The original version, now in PublisherResponseNoStreamTest, still works.
                  // See RESTEASY-1906 "Allow representation of Exception in SSE stream"
    public void testTextErrorDeferred() throws Exception {
        Invocation.Builder request = client.target(generateURL("/text-error-deferred")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Got it", entity);

        // make sure the completion callback was called with with an error
        request = client.target(generateURL("/callback-called-with-error/text-error-deferred")).request();
        response = request.get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>.
     * @tpSince RESTEasy 4.0
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testChunked() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/chunked")).request().rx(FlowableRxInvoker.class);
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        ArrayList<String> list = new ArrayList<String>();
        flowable.subscribe(
                (String s) -> list.add(s),
                (Throwable t) -> logger.error("Error:", t),
                () -> latch.countDown());
        latch.await();
        Assert.assertEquals(Arrays.asList(new String[] { "one", "two" }), list);
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testSse() throws Exception {
        WebTarget target = client.target(generateURL("/sse"));
        List<String> collector = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        SseEventSource source = SseEventSource.target(target).build();
        source.register(evt -> {
            String data = evt.readData(String.class);
            collector.add(data);
            if (collector.size() >= 30) {
                future.complete(null);
            }
        }, t -> {
            logger.error(t.getMessage(), t);
            errors.add(t);
        }, () -> {
            // bah, never called
            future.complete(null);
        });
        source.open();
        future.get();
        source.close();
        Assert.assertEquals(30, collector.size());
        Assert.assertEquals(0, errors.size());
        Assert.assertEquals("0-2", collector.get(0));
        Assert.assertEquals("1-2", collector.get(1));
    }

    /**
     * @tpTestDetails Resource method unsubscribes on close for infinite streams.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testInfiniteStreamsSse() throws Exception {
        WebTarget target = client.target(generateURL("/sse-infinite"));
        List<String> collector = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        SseEventSource source = SseEventSource.target(target).build();
        source.register(evt -> {
            String data = evt.readData(String.class);
            collector.add(data);
            if (collector.size() >= 2) {
                future.complete(null);
            }
        }, t -> {
            logger.error("Error:", t);
            errors.add(t);
        },
                () -> {
                    // bah, never called
                    future.complete(null);
                });
        source.open();
        future.get();
        source.close();
        Assert.assertEquals(2, collector.size());
        Assert.assertEquals(0, errors.size());
        Assert.assertEquals("one", collector.get(0));
        Assert.assertEquals("one", collector.get(1));

        Thread.sleep(5000);
        Client client = ClientBuilder.newClient();
        Invocation.Builder request = client.target(generateURL("/infinite-done")).request();
        Response response = request.get();
        logger.info("part 2");
        String entity = response.readEntity(String.class);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("true", entity);
        client.close();
    }

    /**
     * @tpTestDetails Resource method unsubscribes on close for infinite streams.
     * @tpSince RESTEasy 4.0
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInfiniteStreamsChunked() throws Exception {
        Client client = ClientBuilder.newClient();
        FlowableRxInvoker invoker = client.target(generateURL("/chunked-infinite")).request().rx(FlowableRxInvoker.class);
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        ArrayList<String> list = new ArrayList<String>();
        final Disposable disposable = flowable.subscribe(
                (String s) -> {
                    list.add(s);
                    if (list.size() >= 2)
                        latch.countDown();
                },
                (Throwable t) -> logger.error("Error:", t),
                () -> latch.countDown());
        latch.await();
        client.close();

        Thread.sleep(5000);
        disposable.dispose();
        client = ClientBuilder.newClient();
        Builder request = client.target(generateURL("/infinite-done")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("true", entity);
        client.close();
    }
}
