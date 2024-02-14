package org.jboss.resteasy.test.client;

import java.util.concurrent.CountDownLatch;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.AsyncInvokeResource;
import org.jboss.resteasy.test.client.resource.InContainerClientResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpChapter Client tests
 * @tpSubChapter Performances
 * @tpTestCaseDetails https://issues.jboss.org/browse/RESTEASY-XYZ
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Disabled("Not a functional test")
public class InContainerClientBenchTest extends ClientTestBase {

    static Client nioClient;
    private static Logger log = Logger.getLogger(InContainerClientBenchTest.class);

    static final int ITERATIONS = 4000;
    static final int MAX_CONNECTIONS = 40;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InContainerClientBenchTest.class.getSimpleName());
        war.addClass(InContainerClientBenchTest.class);
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, AsyncInvokeResource.class, InContainerClientResource.class);
    }

    @AfterEach
    public void after() throws Exception {
        if (nioClient != null)
            nioClient.close();
    }

    @Test
    public void testAsyncComposedPost() throws Exception {
        long start = System.currentTimeMillis();
        final String oldProp = System.getProperty("http.maxConnections");
        System.setProperty("http.maxConnections", String.valueOf(MAX_CONNECTIONS));
        nioClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).useAsyncHttpEngine().build();
        WebTarget wt = nioClient.target(generateURL("/test-client"));
        runCallback(wt, "NIO", "client-post post ");
        long end = System.currentTimeMillis() - start;
        log.info("TEST COMPOSED NON BLOCKING IO - " + ITERATIONS + " iterations took " + end + "ms");
        if (oldProp != null) {
            System.setProperty("http.maxConnections", oldProp);
        } else {
            System.clearProperty("http.maxConnections");
        }
    }

    private void runCallback(WebTarget wt, String msg, String expectedResultPrefix) throws Exception {
        CountDownLatch latch = new CountDownLatch(ITERATIONS);
        for (int i = 0; i < ITERATIONS; i++) {
            final String m = msg + i;
            wt.request().async().post(Entity.text(m), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assertions.assertEquals("Got: " + entity, expectedResultPrefix + m, entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                    throw new RuntimeException(error);
                }
            });
        }
        latch.await();
    }
}
