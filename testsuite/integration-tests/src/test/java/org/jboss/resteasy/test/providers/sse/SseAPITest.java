package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseAPITest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseAPITest.class.getSimpleName());
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new RuntimePermission("modifyThread")),
                "permissions.xml");
        List<Class<?>> singletons = new ArrayList<Class<?>>();
        singletons.add(SseAPIImpl.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, SseAPI.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseAPITest.class.getSimpleName());
    }

    // test for RESTEASY-2017:SSE doesn't work with inherited annotations
    @Test
    public void testAnnotaitonInherited() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/apitest/events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                results.add(event.readData(String.class));
                latch.countDown();
            }, ex -> {
                throw new RuntimeException(ex);
            });
            eventSource.open();
            Thread.sleep(1000);
            Client messageClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
            WebTarget messageTarget = messageClient.target(generateURL("/apitest/send"));
            Response response = messageTarget.request().post(Entity.text("apimsg"));
            Assertions.assertEquals(204, response.getStatus());
            boolean result = latch.await(10, TimeUnit.SECONDS);
            Assertions.assertTrue(result, "Waiting for event to be delivered has timed out.");
            messageClient.close();
        }
        Assertions.assertEquals(1, results.size(), "One event message was expected.");
        Assertions.assertTrue(results.get(0).contains("apimsg"),
                "Expected event contains apimsg, but is:" + results.get(0));
        client.close();
    }

}
