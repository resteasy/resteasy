package org.jboss.resteasy.test.exception;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109DefaultExceptionMapper;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109ExceptionRequestFilter;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109SseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RESTEASY3109Test {
    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RESTEASY3109Test.class.getSimpleName());
        war.addClasses(RESTEASY3109DefaultExceptionMapper.class,
                RESTEASY3109ExceptionRequestFilter.class,
                RESTEASY3109SseResource.class);
        return TestUtil.finishContainerPrepare(war, null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, RESTEASY3109Test.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Test()
    public void testException() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(8000),
                () -> {
                    WebTarget target = client.target(generateURL("/sse"));
                    final CountDownLatch latch = new CountDownLatch(1);
                    try (SseEventSource source = SseEventSource.target(target).build()) {
                        source.register(evt -> {
                            Assertions.fail("Should not have seen any results");
                        }, t -> {
                            String s = t.getMessage();
                            Assertions.assertTrue(s.contains("HTTP 500 Internal Server Error"));
                            // We need to count down here as well. Per the SseEventSource.register() the onComplete
                            // callback should not be invoked if the onError callback is invoked.
                            latch.countDown();
                        }, latch::countDown);
                        source.open();
                        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
                    }
                });
    }
}
