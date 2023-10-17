package org.jboss.resteasy.test.undertow;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AsyncIOTest {

    static Client client;
    static UndertowJaxrsServer server;

    @ApplicationPath("/")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(AsyncIOResource.class);
            classes.add(AsyncWriter.class);
            classes.add(BlockingWriter.class);
            return classes;
        }
    }

    @BeforeAll
    public static void init() throws Exception {
        server = new UndertowJaxrsServer().start();
        server.deploy(MyApp.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void stop() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        server.stop();
    }

    @Test
    public void testAsyncIo() throws Exception {
        WebTarget target = client.target(generateURL("/async-io/blocking-writer-on-worker-thread"));
        String val = target.request().get(String.class);
        Assertions.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assertions.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/slow-async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assertions.assertEquals("OK", val);
    }

    @Test
    public void testAsyncIoWrites() throws Exception {
        // 10M
        WebTarget target = client.target(generateURL("/async-io/io/10000000"));
        byte[] val = target.request().get(byte[].class);
        Assertions.assertEquals(10_000_000, val.length);
        // 100M
        target = client.target(generateURL("/async-io/io/100000000"));
        val = target.request().get(byte[].class);
        Assertions.assertEquals(100_000_000, val.length);

        // 10M
        target = client.target(generateURL("/async-io/io/file-range/10000000"));
        val = target.request().get(byte[].class);
        Assertions.assertEquals(10_000_000, val.length);
        // 100M
        target = client.target(generateURL("/async-io/io/file-range/100000000"));
        val = target.request().get(byte[].class);
        Assertions.assertEquals(100_000_000, val.length);
    }
}