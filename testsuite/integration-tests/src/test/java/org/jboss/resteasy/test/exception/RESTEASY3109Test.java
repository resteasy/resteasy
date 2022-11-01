package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109DefaultExceptionMapper;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109ExceptionRequestFilter;
import org.jboss.resteasy.test.exception.resource.RESTEASY3109SseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(Arquillian.class)
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
    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }
    @AfterClass
    public static void close() {
        client.close();
    }

    @Test(timeout = 8000)
    public void testException () throws Exception {
        WebTarget target = client.target(generateURL("/sse"));
        final CountDownLatch latch = new CountDownLatch(1);
        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(evt -> {
                Assert.fail("Should not have seen any results");
            }, t -> {
                String s = t.getMessage();
                Assert.assertTrue(s.contains("HTTP 500 Internal Server Error"));
            }, latch::countDown);
            source.open();
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }
}
