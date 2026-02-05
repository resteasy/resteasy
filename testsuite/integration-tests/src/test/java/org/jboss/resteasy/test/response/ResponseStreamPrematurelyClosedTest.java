package org.jboss.resteasy.test.response;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.TestResourceImpl;
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
public class ResponseStreamPrematurelyClosedTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(ResponseStreamPrematurelyClosedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResourceImpl.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseStreamPrematurelyClosedTest.class.getSimpleName());
    }

    @Test
    public void testStream() throws Exception {
        Builder builder = client.target(generateURL("/test/document/abc/content")).request();

        try (MyByteArrayOutputStream baos = new MyByteArrayOutputStream()) {
            //suggest jvm to do gc and wait the gc notification
            final CountDownLatch coutDown = new CountDownLatch(1);

            List<GarbageCollectorMXBean> gcbeans = ManagementFactory.getGarbageCollectorMXBeans();
            NotificationListener listener = new NotificationListener() {
                public void handleNotification(Notification notification, Object handback) {
                    coutDown.countDown();
                }
            };
            //builder.get().readEntity explicitly on the same line below and not saved in any temp variable
            //to let the JVM try finalizing the ClientResponse object
            try (InputStream ins = builder.get().readEntity(InputStream.class)) {
                for (GarbageCollectorMXBean gcbean : gcbeans) {
                    NotificationEmitter emitter = (NotificationEmitter) gcbean;
                    emitter.addNotificationListener(listener, null, null);
                }
                System.gc();
                coutDown.await(10, TimeUnit.SECONDS);

                ins.transferTo(baos);
                Assertions.assertEquals(10000000, baos.size(), () -> "Received string: " + baos.toShortString());
            } finally {
                //remove the listener
                for (GarbageCollectorMXBean gcbean : gcbeans) {
                    ((NotificationEmitter) gcbean).removeNotificationListener(listener);
                }
            }
        }
    }

    private static class MyByteArrayOutputStream extends ByteArrayOutputStream {

        public String getSubstring(int from, int to) {
            if (from < 0 || to > count) {
                throw new IllegalArgumentException();
            }
            return new String(buf, from, to);
        }

        public String toShortString() {
            int s = size();
            if (s <= 14000) {
                return toString();
            } else {
                return getSubstring(0, 1000) + "..." + getSubstring(s - 13000, 13000);
            }
        }
    }
}
