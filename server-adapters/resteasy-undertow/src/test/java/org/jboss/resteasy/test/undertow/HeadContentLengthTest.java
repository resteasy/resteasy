package org.jboss.resteasy.test.undertow;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HeadContentLengthTest {
    private static UndertowJaxrsServer server;

    @Path("/test")
    public static class Resource {
        @GET
        @Produces("text/plain")
        public String get() {
            return "hello world";
        }
    }

    @ApplicationPath("/base")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(Resource.class);
            return classes;
        }
    }

    @BeforeAll
    public static void init() throws Exception {
        server = new UndertowJaxrsServer().start();
    }

    @AfterAll
    public static void stop() throws Exception {
        server.stop();
    }

    @Test
    public void testHeadContentLength() throws Exception {
        server.deploy(MyApp.class, "/");

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/base/test"));
        Response getResponse = target.request().buildGet().invoke();
        String val = ClientInvocation.extractResult(new GenericType<String>(String.class), getResponse, null);
        Assertions.assertEquals("hello world", val);
        Response headResponse = target.request().build(HttpMethod.HEAD).invoke();
        Assertions.assertEquals(getResponse.getLength(), headResponse.getLength(),
                "HEAD method should return the same Content-Length as the GET method");
        Assertions.assertTrue(getResponse.getLength() > 0);
        client.close();
    }

}
