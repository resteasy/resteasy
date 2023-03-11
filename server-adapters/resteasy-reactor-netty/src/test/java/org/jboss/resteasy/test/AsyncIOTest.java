package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncIOTest {

    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        ResteasyDeployment deployment = ReactorNettyContainer.start();
        deployment.getProviderFactory().register(BlockingWriter.class);
        deployment.getProviderFactory().register(AsyncWriter.class);
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(AsyncIOResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        ReactorNettyContainer.stop();
    }

    @Test
    public void testAsyncIo() throws Exception {
        WebTarget target = client.target(generateURL("/async-io/blocking-writer-on-io-thread"));
        String val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/async-writer-on-io-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/slow-async-writer-on-io-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/blocking-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/slow-async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);
    }
}
