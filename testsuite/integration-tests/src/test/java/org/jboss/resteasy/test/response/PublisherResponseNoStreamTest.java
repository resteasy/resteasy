package org.jboss.resteasy.test.response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.response.resource.AsyncResponseCallback;
import org.jboss.resteasy.test.response.resource.AsyncResponseException;
import org.jboss.resteasy.test.response.resource.AsyncResponseExceptionMapper;
import org.jboss.resteasy.test.response.resource.PublisherResponseNoStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Publisher response type
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PublisherResponseNoStreamTest {

    Client client;

    private static final Logger logger = Logger.getLogger(PublisherResponseNoStreamTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PublisherResponseNoStreamTest.class.getSimpleName());
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.reactivestreams\n"));
        return TestUtil.finishContainerPrepare(war, null, PublisherResponseNoStreamResource.class,
                AsyncResponseCallback.class, AsyncResponseExceptionMapper.class, AsyncResponseException.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PublisherResponseNoStreamTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testText() throws Exception {
        Invocation.Builder request = client.target(generateURL("/text")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(entity.startsWith("[\"0-2\",\"1-2\""));
        Assertions.assertTrue(entity.endsWith(",\"29-2\"]"));

        // make sure the completion callback was called with no error
        request = client.target(generateURL("/callback-called-no-error/text")).request();
        response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>, throws exception immediately.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testTextErrorImmediate() throws Exception {
        Invocation.Builder request = client.target(generateURL("/text-error-immediate")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(444, response.getStatus());
        Assertions.assertEquals("Got it", entity);

        // make sure the completion callback was called with with an error
        request = client.target(generateURL("/callback-called-with-error/text-error-immediate")).request();
        response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>, throws exception in stream.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testTextErrorDeferred() throws Exception {
        Invocation.Builder request = client.target(generateURL("/text-error-deferred")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(444, response.getStatus());
        Assertions.assertEquals("Got it", entity);

        // make sure the completion callback was called with with an error
        request = client.target(generateURL("/callback-called-with-error/text-error-deferred")).request();
        response = request.get();
        Assertions.assertEquals(200, response.getStatus());
        response.close();
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
            if (collector.size() >= 2) {
                future.complete(null);
            }
        },
                t -> {
                    logger.error(t.getMessage(), t);
                    errors.add(t);
                },
                () -> {
                    // bah, never called
                    future.complete(null);
                });
        source.open();
        future.get();
        source.close();
        Assertions.assertEquals(2, collector.size());
        Assertions.assertEquals(0, errors.size());
        Assertions.assertEquals("one", collector.get(0));
        Assertions.assertEquals("two", collector.get(1));
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
            logger.error(t);
            errors.add(t);
        }, () -> {
            // bah, never called
            future.complete(null);
        });
        source.open();
        future.get();
        source.close();
        Assertions.assertEquals(2, collector.size());
        Assertions.assertEquals(0, errors.size());
        Assertions.assertEquals("one", collector.get(0));
        Assertions.assertEquals("one", collector.get(1));

        close();
        setup();
        Thread.sleep(5000);
        Invocation.Builder request = client.target(generateURL("/infinite-done")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("true", entity);
    }
}
