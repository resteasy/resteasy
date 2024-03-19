package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeMessageBodyWriter;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeUser;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseSmokeTest {
    private static final Logger logger = Logger.getLogger(SseSmokeTest.class);
    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseSmokeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseSmokeMessageBodyWriter.class, SseSmokeUser.class,
                SseSmokeResource.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newBuilder().build();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseSmokeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails REST resource with SSE endpoint. Event is sent to the client.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSmoke() throws Exception {
        final List<String> results = new ArrayList<String>();
        WebTarget target = client.target(generateURL("/sse/events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();

        try (SseEventSource eventSource = msgEventSource) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            eventSource.register(event -> {
                countDownLatch.countDown();
                results.add(event.readData(String.class));
            }, e -> {
                throw new RuntimeException(e);
            });
            eventSource.open();
            boolean result = countDownLatch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(result, "Waiting for event to be delivered has timed out.");
        }
        Assertions.assertEquals(1, results.size(), "One message was expected.");
        Assertions.assertEquals(results.get(0), "Zeytin;zeytin@resteasy.org",
                "The message doesn't have expected content.");
    }
}
